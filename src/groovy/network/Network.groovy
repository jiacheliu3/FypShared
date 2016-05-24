package network

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import codebigbrosub.User
import crawler.UserManager

@Log4j
public class Network{
	String ownerName;
	TreeSet<Node> nodes;
	HashSet<Link> links;
	public Network(){
		nodes=new HashSet<>();
		links=new HashSet<>();
	}
	public void addNode(Node n){
		nodes.add(n);
	}
	//use a more recognizable name
	public void addNodeGroup(Collection c){
		nodes.addAll(c);
	}
	public void addLink(Link l){
		links.add(l);
	}
	public void addLinkGroup(Collection c){
		if(c.size()==0)
			return;
		links.addAll(c);
	}
	public void addLinkGroupWithWeight(Collection<Link> c){
		if(c==null){
			log.error "Links are null.";
		}
		c.each{link->
			//check if the same link already exists, hashset won't dispose items with same hash code
			String source=link.source;
			String target=link.target;
			Link dup=findLink(source,target);
			if(dup==null){
				links.add(link);
			}else{
				log.debug "Found existing link from ${source} to ${target}. Increase the weight of this link by ${link.value}";
				dup.value+=link.value;
			}
		}
	}
	public Node findNode(String name){
		//no choice but to linear search
		Node answer;
		for(Node n:nodes){
			if(n.name==name){
				answer=n;
				break;
			}
		}
		if(answer==null){
			log.error "Cannot find the node with name ${name}!";
		}
		return answer;
	}
	//assign groups within the network nodes
	public void assignGroups(){
		//find out going and in coming sets
		Set<String> outGoing=new HashSet<>();
		Set<String> inComing=new HashSet<>();
		links.each{link->
			outGoing.add(link.source);
			inComing.add(link.target);
		}
		
		//find single sided and double sided nodes
		/*
		 * out not in: 2
		 * in not out: 3
		 * double: 4
		 * */
		def outNotIn=outGoing-inComing;
		def inNotOut=inComing-outGoing;
		def both=outNotIn.intersect(inNotOut);
		log.debug "${outNotIn} has no incoming links while ${inNotOut} has no outgoing ones";
		
		nodes.each{node->
			if(outNotIn.contains(node.name)){
				node.group=2;
			}
			if(inNotOut.contains(node.name)){
				node.group=3;
			}
			if(both.contains(node.name)){
				node.group=4;
			}
			
		}
	}
	//keep only those that have appeared more than once in the links
	public void keepCore(){
		log.info "Start network optimization.";
		Map<String,Integer> appr=new HashMap<>();
		def putInMap={map,thing->
			if(map.containsKey(thing))
				map[thing]+=1;
			else
				map.put(thing,1);
		}
		links.each{link->
			String s=link.source;
			String t=link.target;
			putInMap(appr,s);
			putInMap(appr,t);
		}
		log.info "Finished appearance count over all nodes.";
		Set<String> vip=appr.findAll {it.value>1}.collect {it.key} as Set<String>;
		log.debug "The top active ones are: "+vip;
		//what if only a few are kept;
		def iter=appr.iterator();
		while(vip.size()<=20){
			def entry=iter.next();
			vip.add(entry.key);
		}
		log.debug "Got the top 20 active users: "+vip;
		//keep only the links that are between them
		def toRemove=[];
		links.each{link->
			if(vip.contains(link.source)&&vip.contains(link.target)){}
			else{
				toRemove.add(link);
			}
		}
		log.debug "${toRemove.size()} links to remove.";
		toRemove.each{
			links.remove(it);
		}
		log.debug "Finished filtering.";
		//remove nodes again
		Set<String> namesLeft=new HashSet<>();
		links.each{link->
			namesLeft.add(link.source);
			namesLeft.add(link.target);
		}
		def removeNodes=[];
		nodes.each{node->
			if(namesLeft.contains(node.name)){
				//keep the node
			}
			else{
				removeNodes.add(node);
			}
		}
		removeNodes.each{
			nodes.remove(it);
		}
		//all clean, debrief now
		debrief();
	}
	public void clean(){
		//get rid of links with same target as source
		Set<String> nodeNames=new HashSet<>();
		def linksToRemove=[];
		links.each{link->
			String s=link.source;
			String t=link.target;
			if(s==t){
				log.error "The link from ${s} to ${t} will be eliminated.";
				linksToRemove.add(link);
			}else{
				nodeNames.add(s);
				nodeNames.add(t);
			}
		}
		log.debug "The network has unique nodes: ${nodeNames}";
		//remove the circle links
		linksToRemove.each{
			links.remove(it);
		}
		//get rid of nodes that do not have links
		def nodesToRemove=[];
		nodes.each{node->
			String n=node.name;
			if(!nodeNames.contains(n)){
				log.error "Node ${n} has no links.";
				nodesToRemove.add(node);
			}
		}
		nodesToRemove.each{
			nodes.remove(it);
		}
		log.info "The network is cleaned.";
		this.debrief();
	}
	public boolean isLarge(){
		return nodes.size()>100;
	}
	public void debrief(){
		log.info "${nodes.size()} Nodes: "+nodes;
		log.info "${links.size()} Links: "+links;
	}
	//use linear search for now as there is no better way found
	public Link findLink(String source,String target){
		Link link;
		for(Link l:links){
			if(l.source==source&&l.target==target){
				link=l;
			}
		}
		return link;
	}
	//for the calculated pagerank, match them back to the nodes
	public void addPageRank(Map pagerank){
		pagerank.each{name,value->
			Node node=nodes.find {it.name==name}
			if(node==null){
				log.error "Cannot find node with name ${name}!";
			}else{
				node.pagerank=value;
			}
		}
		log.info "Added pagerank to nodes in network.";
	}
	public String toJson(){
		String networkJson=new JsonBuilder(this).toPrettyString();
		log.info "Converted network to json";
		String noLineWrap=networkJson.replaceAll("\\n","");
		//log.debug "After replaced all line wrap: "+noLineWrap;

		return noLineWrap;
	}
	//get the top num users that are recorded in db. the user should already be saved in stat step.
	public List<User> findTopActive(int num){
		log.info "Looking for top ${num} users to crawl further.";
		//put links in a treemap by value
		Map<Integer,List> linkMap=new TreeMap<>(Collections.reverseOrder());
		links.each{link->
			int value=link.value;
			if(value==null){
				log.error "Link ${link} has no value!";
			}
			if(linkMap.containsKey(value)){
				linkMap[value].add(link);
			}else{
				def arr=[];
				arr.add(link);
				linkMap.put(value,arr);
			}
		}
		//log.debug "The links are sorted in weight: "+linkMap;
		
		//stop at num users
		int count=0;
		Set<User> userList=new HashSet<>();
		tobreak:
		for(def e:linkMap){
			log.debug "Putting users in link weight ${e.key}";
			def theLinks=e.value;
			for(int i=0;i<theLinks.size();i++){
				if(count>=num){
					break tobreak;
				}
				Link thisLink=theLinks[i];
				String s=thisLink.source;
				String t=thisLink.target;
				//find the user from s and t which is not the user
				def toGo=[];
				//check owner name
				boolean hasName;
				if(ownerName==null||ownerName==""){
					log.error "This network has no owner name!";
					hasName=false;
				}else{
					hasName=true;
				}
				if(hasName){
					if(s!=ownerName)
						toGo.add(s);
					if(t!=ownerName)
						toGo.add(t);
				}else{
					toGo.add(s);
					toGo.add(t);
				}
				//find the user according to name
				if(toGo.size()==0){
					log.error "The link ${thisLink} does not have a valid user to go!";
					continue;
				}else{
					toGo.each{theName->
						User v=UserManager.retrieveUser(theName);
						if(v==null){
							log.error "Null user retrieved by name ${theName}";
						}else{
							userList.add(v);
						}
					}
				}
				
			}
		}
		log.debug "The namelist to go next: "+userList;
		return userList as List<User>;
	}
	public static class Node implements Comparable<Node>{
		String name;
		int group;
		double pagerank;

		@Override
		public String toString(){
			return name;
		}
		@Override
		public int hashCode(){
			return name.hashCode();
		}

		@Override
		public int compareTo(Node other){

			return name.compareTo(other.name);
		}
	}
	public static class Link{
		String source;
		String target;
		int value;

		@Override
		public String toString(){
			return "${source}-->${target}";
		}
		@Override
		public int hashCode(){
			return source.hashCode()+target.hashCode();
		}
	}

	public static void main(String[] args){
		//try find the node by name
		Network n1=new Network();

		def nodes=["P1", "P2", "P3"];
		int c=0;
		nodes.each{
			Node n=new Node(name:it,group:c);
			n1.addNode(n);
			c+=5;
		}

	}
}
