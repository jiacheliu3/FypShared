package network

import groovy.util.logging.Log4j
import network.Network.Link
import network.Network.Node
import codebigbrosub.Job
import codebigbrosub.User

@Log4j
class ComplexNetworkGenerator {
	Job job;//save a reference of job for the sake of logging
	public ComplexNetworkGenerator(Job job){
		this.job=job;
		log.debug "Set job reference in ComplexNetworkGenerator.";
	}
	//calculate the four kinds of interactions as the same and count the weight on each link
	public Map generateDeepFullNetwork(User u){
		HashMap<String,Network> networks=new HashMap<>();
		if(u==null){
			log.error "Passed an empty user to network generator!";
			//generate new empty network
			Network empty=new Network();
			networks.put("all",empty);
			return networks;
		}
		//generate the core network surrounding the user
		Network core=generateShallowFullNetwork(u);

		//for all the user's friends, generate their sub networks
		Set<String> friends=findConnected(u);
		log.info "Found ${friends.size()} users connected to this subject. ";
		friends.each{theName->
			//get the user by name
			User v=callTheUser(theName);
			if(v==null){
				log.info "No network known for this user";
			}else{
				Network sub=generateShallowFullNetwork(v);
				deepMergeNetwork(core,sub);
			}
		}
		log.info "The core network is merged with the sub ones";
		//clean the network
		core.clean();
		core.debrief();

		//calculate page rank on directed network
		def pagerank=networkPagerank(core);
		log.info "Calculated the pagerank: "+pagerank;
		core.addPageRank(pagerank);

		//find cliques in the network
		def cliques=findCliques(core);
		networks.put("all",core);
		return networks;
	}
	public Network generateShallowFullNetwork(User u){
		String userName=u.weiboName;
		//generate the set of all users in the interaction
		Map<String,Integer> outlinks=getOutLinkMap(u);
		Map<String,Integer> inlinks=getInLinkMap(u);

		//generate nodes
		Set<String> all=new HashSet<>();
		all.addAll(outlinks.keySet());
		all.addAll(inlinks.keySet());
		log.info "All friends: "+all;
		TreeSet<Node> nodes=new TreeSet<>();
		//decide group
		int group=1;

		//generate network
		Network simpleNetwork=new Network();
		//put user in first
		Node userNode=new Node(name:userName,group:group);
		simpleNetwork.addNode(userNode);
		//put friends in
		all.each{
			Node n=new Node(name:it,group:group);
			simpleNetwork.addNode(n);
		}
		//generate links
		outlinks.each{k,v->
			if(userName!=k){
				Link l=new Link(source:userName,target:k,value:v);
				simpleNetwork.addLink(l);
			}
		}
		inlinks.each{k,v->
			if(userName!=k){
				Link l=new Link(source:k,target:userName,value:v);
				simpleNetwork.addLink(l);
			}
		}
		log.info "The shallow full network is generated.";
		simpleNetwork.debrief();

		return simpleNetwork;
	}
	public void deepMergeNetwork(Network core,Network sub){
		//merge the nodes
		log.info "The core network has ${core.nodes.size()} nodes before merge.";
		core.addNodeGroup(sub.nodes);
		log.info "The core network has ${core.nodes.size()} nodes after merge.";

		//merge the links
		log.info "The core network has ${core.links.size()} links before merge.";
		core.addLinkGroupWithWeight(sub.links);
		log.info "The core network has ${core.links.size()} links after merge.";
	}
	public Map generateDeepSimpleNetwork(User u){
		HashMap<String,Network> networks=new HashMap<>();
		if(u==null){
			log.error "Passed an empty user to network generator!";
			//generate new empty network
			Network empty=new Network();
			networks.put("all",empty);
			return networks;
		}
		//generate the core network surrounding the user
		Network core=generateShallowSimpleNetwork(u);

		//for all the user's friends, generate their sub networks
		Set<String> friends=findConnected(u);
		log.info "Found ${friends.size()} users connected to this subject. ";


		friends.each{theName->
			//get the user by name
			User v=callTheUser(theName);
			if(v==null){
				log.info "No network known for this user";
			}else{
				Network sub=generateShallowSimpleNetwork(v);
				mergeNetwork(core,sub);
			}
		}
		log.info "The core network is merged with the sub ones";
		//clean the network
		core.clean();
		core.debrief();
		//calculate page rank on directed network
		def pagerank=networkPagerank(core);
		log.info "Calculated the pagerank: "+pagerank;
		core.addPageRank(pagerank);
		//find cliques in the network
		def cliques=findCliques(core);
		networks.put("all",core);
		return networks;
	}
	//the weight is the kind of interaction between users
	public Network generateShallowSimpleNetwork(User u){
		String userName=u.weiboName;
		//generate the set of all users in the interaction
		Set<String> outlinks=getOutLinkSet(u);
		Set<String> inlinks=getInLinkSet(u);

		//generate nodes
		Set<String> all=new HashSet<>();
		all.addAll(outlinks);
		all.addAll(inlinks);
		log.info "All friends: "+all;
		TreeSet<Node> nodes=new TreeSet<>();
		//decide group
		int group=1;

		//generate network
		Network simpleNetwork=new Network();
		//put user in first
		Node userNode=new Node(name:userName,group:1);
		simpleNetwork.addNode(userNode);
		//put friends in
		all.each{
			Node n=new Node(name:it,group:group);
			simpleNetwork.addNode(n);
		}
		//generate links
		outlinks.each{
			if(userName!=it){
				Link l=new Link(source:userName,target:it,value:1.0);
				simpleNetwork.addLink(l);
			}
		}
		inlinks.each{
			if(userName!=it){
				Link l=new Link(source:it,target:userName,value:1.0);
				simpleNetwork.addLink(l);
			}
		}
		log.info "The shallow network is generated.";
		simpleNetwork.debrief();

		return simpleNetwork;
	}
	//handles undirected pagerank
	public static Map networkPagerank(Network network){
		//convert data format
		Set<Node> nodes=new HashSet<>();
		Set<Map> links=new HashSet<>();
		network.nodes.each{
			nodes.add(new Node(name:it.name,group:it.group));
		}
		network.links.each{
			links.add(['source':it.source,'target':it.target,'weight':it.value]);
		}
		log.info "${nodes.size()} nodes and ${links.size()} links passed to pagerank study";
		//calculate pagerank
		def pagerank=PageRankCalc.calculatePageRank(nodes,links,true);
		log.info "Pagerank of network calculated:"+pagerank;
		return pagerank;
	}
	//find the cliques in a network, pivot is the center point of the network
	public List findCliques(Network network){
		CliqueFinder cf=new CliqueFinder();
		def cliques=cf.findClique(network);//a list of cliques

		//assign the nodes in network to groups
		int group=2;//keep group 1 for the general users that are not in any cliques
		cliques.each{clique->
			log.info "Assigning group number ${group} to clique ${clique}";
			for(int i=0;i<clique.size();i++){
				String name=clique[i];
				Node n=network.findNode(name);
				n.group=group;
			}
			group++;
		}
		log.info "Assigned ${cliques.size()} groups.";

		return cliques;
	}

	//merge the sub-network into the main one
	public void mergeNetwork(Network core,Network sub){
		//merge the nodes
		log.info "The core network has ${core.nodes.size()} nodes before merge.";
		core.addNodeGroup(sub.nodes);
		log.info "The core network has ${core.nodes.size()} nodes after merge.";

		//merge the links
		log.info "The core network has ${core.links.size()} edges before merge.";
		core.addLinkGroup(sub.links);
		log.info "The core network has ${core.links.size()} edges after merge.";
	}
	//find those who are connected to this user
	public Set<String> findConnected(User u){
		HashSet<String> friends=new HashSet<>();
		friends.addAll(u.forwarded?.keySet());
		friends.addAll(u.forwarding?.keySet());
		friends.addAll(u.commenting?.keySet());
		friends.addAll(u.commented?.keySet());
		friends.addAll(u.liked?.keySet());
		friends.addAll(u.liking?.keySet());
		friends.addAll(u.mentioned?.keySet());
		friends.addAll(u.mentioning?.keySet());
		println("friends of ${u.weiboName} are "+friends);
		return friends;
	}
	public User callTheUser(String name){
		User u;
		User.withTransaction{
			u=User.find("from User as u where u.weiboName=?",[name]);
		}
		if(u==null){
			log.info "User ${name} doesn't exist in the records.";
		}
		return u;
	}
	//get the users who have reposted/commented/liked/mentioned the user
	public Set<String> getOutLinkSet(User u){
		HashSet<String> friends=new HashSet<>();
		friends.addAll(u.forwarding?.keySet());
		friends.addAll(u.commenting?.keySet());
		friends.addAll(u.liking?.keySet());
		friends.addAll(u.mentioning?.keySet());
		log.info("Nodes receiving a link from ${u.weiboName} are "+friends);
		return friends;
	}
	//get the users who have been reposted/commented/liked/mentioned by the user
	public Set getInLinkSet(User u){
		HashSet<String> friends=new HashSet<>();
		friends.addAll(u.forwarded?.keySet());
		friends.addAll(u.commented?.keySet());
		friends.addAll(u.liked?.keySet());
		friends.addAll(u.mentioned?.keySet());

		log.info("Nodes giving a link to ${u.weiboName} are "+friends);
		return friends;
	}
	//combine the link weight from each user
	public Map getOutLinkMap(User u){
		def friends=[:];
		mergeMap(friends,u.forwarding);
		mergeMap(friends,u.commenting);
		mergeMap(friends,u.liking);
		mergeMap(friends,u.mentioning);
		log.info "After merging all the weights the out link map is "+friends;
		return friends;
	}
	public Map getInLinkMap(User u){
		def friends=[:];
		mergeMap(friends,u.forwarded);
		mergeMap(friends,u.commented);
		mergeMap(friends,u.liked);
		mergeMap(friends,u.mentioned);
		log.info "After merging all the weights the in link map is "+friends;
		return friends;
	}
	public void mergeMap(Map largeMap,Map smallMap){
		if(smallMap.size()==0){
			return;
		}else{
			smallMap.each{k,v->
				//ensure value is number
				def value=ensureNumber(v);
				if(largeMap.containsKey(k)){
					largeMap[k]+=value;
				}else{
					largeMap.put(k,value);
				}

			}
		}
	}
	public ensureNumber(def v){
		def value;
		if(v instanceof Integer){
			value=v;
		}else if(v instanceof Double){
			value=v;
		}else if(v instanceof String){
			if(v.contains('.')){
				try{
					value=Double.parseDouble(v);
				}catch(ClassCastException e){
					log.error "Cannot cast value ${v}(${v.class}) to Double!";
					value=null;
				}
			}else{
				try{
					value=Integer.parseInt(v);
					
				}catch(ClassCastException e){
					log.error "Cannot cast value ${v}(${v.class}) to Integer!";
					value=null;
				}
			}
		}
		return value;
	}
}
