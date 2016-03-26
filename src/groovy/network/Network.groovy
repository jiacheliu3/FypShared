package network

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j

@Log4j
public class Network{
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
		log.info "The network has unique nodes: ${nodeNames}";
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
		log.info "Converted network to json: "+networkJson;
		String noLineWrap=networkJson.replaceAll("\\n","");
		log.debug "After replaced all line wrap: "+noLineWrap;
		
		return noLineWrap;
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
		float value;

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
		
		def nodes=["P1","P2","P3"];
		int c=0;
		nodes.each{
			Node n=new Node(name:it,group:c);
			n1.addNode(n);
			c+=5;
		}
		
	}
}
