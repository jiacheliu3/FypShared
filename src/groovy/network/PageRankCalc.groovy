package network
import groovy.util.logging.Log4j
import Jama.Matrix


@Log4j
class PageRankCalc {
	public static double damping=0.85;
	public static double threshold=0.00001;


	public static void setDamping(double d){
		damping=d;
	}
	public static void setThreshold(double d){
		threshold=d;
	}
	public static Map<String,Integer> constructIndexMap(Collection<String> nodes){
		Map<String,Integer> indexMap=new HashMap<>();
		int count=0;
		for(String node:nodes){

			indexMap.put(node, count);

			count++;
		}
		//log.debug "Contructed index: "+indexMap;
		return indexMap;
	}
	public static Matrix constructTransitionMatrix(Collection<String> nodes,Set<Map> edges,Map<String,Integer> indexMap,boolean isDirected){
		int n=nodes.size();
		int x=edges.size();
		log.info("${n} nodes and ${x} edges");
		//construct adjacency matrix
		log.info "Begin construction of adjacency matrix.";
		log.debug "Got index map "+indexMap;
		log.debug "All links are: "+edges;
		Map<String,Integer> sizeMap=new HashMap<>();//out-degree of each node
		Matrix A=new Matrix(n,n);
		HashSet<String> notFound=new HashSet<>();
		for(Map<String,String> m:edges){
			String source=m["source"].trim();
			String target=m["target"].trim();
			double weight=m["weight"];

			Integer row=indexMap[source];
			Integer col=indexMap[target];
			boolean canCal=true;
			if(row==null){
				//activate dumb map scan to find it
				for(def e:indexMap){
					String s=e.key;
					if(s.contains(source)){
						log.info "Got one similar entry in index map:"+s+" on ${e.value} place";
						log.debug "Comparing the length of two:\n The target one ${source}: ${source.length()}, The found one ${s}:${s.length()}";
						log.debug "Compare the bytes: Target one ${source.getBytes()}, found one:${s.getBytes()}"
						row=e.value;
					}
				}
				if(row==null){
					log.error "After scanning the whole map ${source} is still not found.";
					notFound.add(source);
				}
				
				canCal=false;
			}
			if(col==null){
				//activate dumb map scan to find it
				for(def e:indexMap){
					String s=e.key;
					if(s.contains(target)){
						log.info "Got one similar entry in index map:"+s+" on ${e.value} place";
						log.debug "Comparing the length of two:\n The target one ${target}: ${target.length()}, The found one ${s}:${s.length()}";
						log.debug "Compare the bytes: Target one ${target.getBytes()}, found one:${s.getBytes()}"
						col=e.value;
					}
				}
				if(col==null){
					log.error "After scanning the whole map ${target} is still not found.";
					notFound.add(target);
				}
				canCal=false;
			}
			if(canCal){
				//else update the degree of the node
				double value=A.get(row,col);
				double newValue=value+weight;
				//log.info "The value is updated from ${value} to ${newValue}";
				A.set(row, col, value+weight);

				if(sizeMap.containsKey(source)){
					sizeMap[source]+=1;
				}else{
					sizeMap.put(source,1);
				}
				//if the graph is not directed, the target also gives out the link
				if(!isDirected){
					double v=A.get(row,col);
					A.set(col, row, v+weight);

					if(sizeMap.containsKey(target)){
						sizeMap[target]+=1;
					}else{
						sizeMap.put(target,1);
					}
				}
			}
		}
		//report which nodes are not found
		log.error "Some nodes are not found in indexMap"+notFound;
		log.info "Now check why they are not found:";
		notFound.each{theName->
			def f=indexMap[theName];
			log.info "${theName} is on ${f} in index map";
			for(def e:indexMap){
				String s=e.key;
				if(s.contains(theName)){
					log.info "Got one similar entry in index map:"+s+" on ${e.value} place";
					log.info "Comparing the length of two:\n The target one ${theName}: ${theName.length()}, The found one ${s}:${s.length()}";
					f=e.value;
				}
			}
			if(f==null){
				log.error "After scanning the whole map ${theName} is still not found.";
			}
		}
		log.info "The weight matrix is: ";
		log.info A.getArray().toString();
		//calculate the sum of weights
		double totalWeight=0;
		edges.each{edge->
			totalWeight+=edge.weight;
		}
		log.info "The total weight of all edges is: ${totalWeight}";
		//construct transition matrix
		Matrix M=new Matrix(n,n);
		for(def e:indexMap){
			String nodeName=e.key;
			int row=indexMap[nodeName];
			if(row==null){
				log.error "Row is not located in transition matrix construction "+nodeName;
				continue;
			}
			int size;
			if(sizeMap.containsKey(nodeName)){
				size=sizeMap[nodeName];
			}
			else{
				size=0;
			}
			if(size==0){
				double s=1.0/(double)n;
				for(int j=0;j<n;j++){
					M.set(row, j,s);
					//M.set(row, j,0.0);//set node with no out link as 0
				}
			}
			else{
				double sigma=0;
				for(int i=0;i<n;i++){
					double d=A.get(row, i);
					sigma+=d;//when weight of every edge is 1, sigma=size
				}
				for(int i=0;i<n;i++){
					double w=A.get(row, i);//w=0 when no edge
					M.set(row,i,w/sigma);
					//M.set(row,i,w/totalWeight);//normalize all edges
				}
			}

		}
		//M.print(5,2);
		log.info("Transition matrix complete.");
		log.info "The transition matrix is: ";
		log.info M.getArray().toString();
		return M;

	}
	public static double calculateError(Matrix A, Matrix B){
		Matrix diff=A.minus(B);
		return diff.normF();//sqrt of squares of all elements
	}
	public static Map<String,Double> calculatePageRank(Collection<String> nodes,Set<Map> edges,boolean isDirected){

		int n=nodes.size();
		if(n==0){
			log.error "No nodes passed to pagerank calc!";
		}
		//construct index map storing all nodes and index
		Map<String,Integer> indexMap=constructIndexMap(nodes);
		//construct transition matrix
		Matrix M=constructTransitionMatrix(nodes,edges,indexMap,isDirected);
		//initialize the matrix
		Matrix N=new Matrix(1,n,1.0/n);
		//iteratively calculate
		Matrix I=new Matrix(1,n,(1.0-damping)/n);
		log.info("Start to compute pagerank");
		//		//approximation of times of iterations
		//		int iter=(int)(Math.log(n)/Math.log(10))+1;
		//		for(int i=0;i<iter;i++){
		//			N=N.times(M.times(damping)).plus(I);
		//		}

		boolean converged=false;
		int times=0;
		while(!converged){
			Matrix nextN=N.times(M.times(damping)).plus(I);
			//nextN.print(0, n-1);
			double error=calculateError(N,nextN);
			N=nextN;
			if(error<=threshold){
				converged=true;
			}
			times++;
		}
		log.info("Converged in ${times} iterations.");
		//assign ranks to nodes
		Map<String,Double> pageRank=new LinkedHashMap<>();
		def findName={number->
			for(def e:indexMap){
				if(e.value==number)
					return e.key;
			}
		}
		double[][] ranks=N.getArray();
		for(int i=0;i<n;i++){
			pageRank.put(findName(i), ranks[0][i])
		}
		return pageRank;

	}
	public static Map<String,Double> normalize(Map<String,Double> pagerank){
		double sum=0.0;
		int count=pagerank.size();
		Map<String,Double> normalizedRank=new LinkedHashMap<>();
		pagerank.each{
			sum+=it.value;

		}
		pagerank.each{
			normalizedRank.put(it.key, it.value/sum);
		}
		return normalizedRank;
	}
	public static void main(String[] args){
		//test example from http://en.wikipedia.org/wiki/PageRank
		def nodes=[
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k'
		];
		def set=[
			['source':'b','target':'c','weight':1],
			['source':'c','target':'b','weight':1],
			['source':'d','target':'a','weight':1],
			['source':'d','target':'b','weight':1],
			['source':'e','target':'b','weight':1],
			['source':'e','target':'d','weight':1],
			['source':'e','target':'f','weight':1],
			['source':'f','target':'b','weight':1],
			['source':'f','target':'e','weight':1],
			['source':'g','target':'b','weight':1],
			['source':'g','target':'e','weight':1],
			['source':'h','target':'b','weight':1],
			['source':'h','target':'e','weight':1],
			['source':'i','target':'b','weight':1],
			['source':'i','target':'e','weight':1],
			['source':'j','target':'e','weight':1],
			['source':'k','target':'e','weight':1]
		];
		Set<Map> edges=new HashSet<>();
		edges.addAll(set);

		def yarank=calculatePageRank(nodes,edges,true);
		println yarank;
		yarank=normalize(yarank);
		println yarank;
	}

}
