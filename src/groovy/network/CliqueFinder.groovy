package network

import grails.converters.JSON
import groovy.util.logging.Log4j
import Jama.Matrix
import network.Network
import network.Network.Link
import network.Network.Node
@Log4j
class CliqueFinder {
	Map<String,Integer> nodeIndex;
	Map<Integer,String> indexNode;
	public List findClique(Network network){
		if(network==null){
			log.error "Null network. No clique found.";
			return [];
		}
		//check the integrity of network
		if(network.nodes==null){
			log.error "No nodes for the network";
			return [];
		}else if(network.nodes.size()==0){
			log.error "Node size is 0 for network";
			return [];
		}
		if(network.links==null){
			log.error "No links for the network";
			return [];
		}else if(network.links.size()==0){
			log.error "Link size is 0 for the network";
			return [];
		}
		//construct index for later use
		buildIndices(network);
		//construct adjacent matrix
		Matrix A=constructAdjacencyMatrix(network);
		//construct double adjacent matrix
		Matrix D=constructDoubleAdajencyMatrix(A);
		//calculate triple times
		Matrix M=constructCubicMatrix(D);
		log.info "The cubic of matrix is ";
		log.info M.getArray();
		//find clique
		def cliques=findAllCliques(M);

		log.info "The all found cliques are ${cliques}";

		return cliques;
	}
	public void buildIndices(Network network){
		//build indices for nodes
		nodeIndex=new HashMap<>();
		indexNode=new HashMap<>();

		def nodes=network.nodes;
		int index=0;
		nodes.each{node->
			nodeIndex.put(node.name,index);
			indexNode.put(index,node.name);
			index++;
		}
		int siz=nodeIndex.size();
		log.debug "Constructed node index for ${nodeIndex.size()} nodes";
		log.debug nodeIndex;
	}
	public Matrix constructAdjacencyMatrix(Network network){
		int siz=nodeIndex.size();
		//build matrix, row: source, column: target
		Matrix A=new Matrix(siz,siz);

		def links=network.links;
		links.each{link->
			boolean validLink=true;
			if(!nodeIndex.containsKey(link?.source)){
				log.error "The source of link ${link?.source} is not contained in the indices!";
				validLink=false;
			}
			if(!nodeIndex.containsKey(link?.source)){
				log.error "The source of link ${link?.source} is not contained in the indices!";
				validLink=false;
			}
			if(validLink){
				String source=link.source;
				String target=link.target;
				def ss=nodeIndex[source];
				def tt=nodeIndex[target];
				//log.debug "Link source ${source} has index ${ss} and target ${target} has index ${tt}.";
				if(ss==null||tt==null){
					log.error "Index is null for link ${link}. What happened?!";
				}else{
					int s=ss;
					int t=tt;
					A.set(s,t,1.0);
				}
			}

		}
		return A;
	}
	//if node i and j has links to each other, mark Dij as 1
	public Matrix constructDoubleAdajencyMatrix(Matrix A){
		int siz=A.getRowDimension();
		Matrix D=new Matrix(siz,siz);

		int x=0;
		int count=0;
		//i for row
		for(int i=0;i<siz;i++){
			//j for column, only need to check column from x on
			for(int j=x;j<siz;j++){
				//check
				double dij=A.get(i,j);
				double dji=A.get(j,i);
				if(dij==1.0&&dji==1.0){
					log.debug "Found double-side link from node ${indexNode[i]} to ${indexNode[j]}.";
					D.set(i,j,1.0);
					D.set(j,i,1.0);
				}

				count++;
			}
			x++;
		}
		int std=(1+siz)*siz/2;
		//println "Finished constructing double-sided adjacency matrix. ${count} comparisons in total. There should be ${std} comparisons.";
		return D;
	}
	public Matrix constructCubicMatrix(Matrix M){
		Matrix M_3=M.times(M).times(M);
		return M_3;
	}
	public List findAllCliques(Matrix M){
		int siz=M.getRowDimension();
		def cliques=[];

		//find the big group of all cliques
		def cliqueRaw=[];
		for(int i=0;i<siz;i++){
			int m=M.get(i,i);
			if(m!=0){
				cliqueRaw.add(i);
			}
		}
		//break the clique into smaller ones
		while(cliqueRaw.size()>0){
			int index=cliqueRaw[0];
			log.info "Extracting the clique containing node index ${index}";
			//find all columns that are not zero in row ${index}
			double[][] allRows=M.getArray();
			double[] row=allRows[index];
			//println "Row ${index}:${row}";
			def nonzero=[];
			for(int i=0;i<row.length;i++){
				if(cliqueRaw.contains(i)){
					int r=row[i];
					if(r!=0.0)
						nonzero.add(i);
				}else{
					log.debug "Index ${i} is not included in any cliques.";
				}
			}
			log.debug "The clique members are ${nonzero}";
			//match back to names are add the cliques
			def clique=new HashSet<String>();
			nonzero.each{idx->
				String name=indexNode[idx];
				clique.add(name);
			}
			cliques.add(clique);
			//remove the items from candidate clique members
			cliqueRaw.removeAll(nonzero);
			log.debug "Now the left clique candidates are: ${cliqueRaw}";
		}

		log.info "The final separated cliques are ${cliques}";
		return cliques;
	}
	public static void main(String[] args){
		//make networks and find cliques
		def rawLinks1="""[
  {
    "source": "P1",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P1",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P1",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P1",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P3",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P3",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P1",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P5",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P5",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P5",
    "target": "P8",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P5",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P7",
    "target": "P5",
    "value": 1
  },
  {
    "source": "P7",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P8",
    "target": "P5",
    "value": 1
  },
  {
    "source": "P8",
    "target": "P9",
    "value": 1
  },
  {
    "source": "P9",
    "target": "P8",
    "value": 1
  }
]""";
		def links1=JSON.parse(rawLinks1);
		def nodes1=new ArrayList<String>();
		nodes1.addAll(["P1", "P2", "P3", "P5", "P4", "P6", "P7", "P8", "P9"]);
		Network n1=new Network();
		nodes1.each{name->
			Node n=new Node(name:name,group:1);
			n1.addNode(n);
		}
		links1.each{
			Link l=new Link(source:it.source,target:it.target,value:it.value);
			n1.addLink(l);
		}
		println "The sample network is ${n1} and IMA find cliques outta it.";

		CliqueFinder cf=new CliqueFinder();
		def cliques=cf.findClique(n1);
		println "The found cliques are ${cliques}";

		def rawLinks2="""
[
  {
    "source": "P1",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P1",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P1",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P1",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P3",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P3",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P1",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P10",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P10",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P10",
    "target": "P8",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P10",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P7",
    "target": "P10",
    "value": 1
  },
  {
    "source": "P7",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P8",
    "target": "P10",
    "value": 1
  },
  {
    "source": "P8",
    "target": "P9",
    "value": 1
  },
  {
    "source": "P9",
    "target": "P8",
    "value": 1
  }
]""";
		def links2=JSON.parse(rawLinks2);
		def nodes2=new ArrayList<String>();
		nodes2.addAll(["P1", "P2", "P3", "P10", "P4", "P6", "P7", "P8", "P9"]);
		Network n2=new Network();
		nodes2.each{name->
			Node n=new Node(name:name,group:1);
			n2.addNode(n);
		}
		links2.each{
			Link l=new Link(source:it.source,target:it.target,value:it.value);
			n2.addLink(l);
		}
		println "The sample network is ${n2} and IMA find cliques outta it.";

		CliqueFinder cf2=new CliqueFinder();
		def cliques2=cf2.findClique(n2);
		println "The found cliques are ${cliques2}";
	}
}
