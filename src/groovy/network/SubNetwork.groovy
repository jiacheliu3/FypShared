package network

class SubNetwork {
	HashSet<String> nodes;
	HashMap<String,String> edges;
	public SubNetwork(){
		nodes=new HashSet<>();
		edges=new HashMap<>();
	}
	public addNode(String n){
		nodes.add(n);
	}
	public addEdge(String s,String e){
		edges.put(s,e);
	}
}
