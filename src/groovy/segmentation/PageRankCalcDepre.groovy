package segmentation

import java.util.Map;
import java.util.Set;

import Jama.Matrix;

class PageRankCalcDepre {
	static Set<String> nodeSet=new HashSet<>();
	static Map<String,Set<String>> edgeMap=new HashMap<>();
	static Map<Integer,String> orderMap=new TreeMap<>();
	static Map<String,Double> rankMap=new LinkedHashMap<>();
	static double dampening=0.85;
	//by default visualize 1000 top keywords
	static int outputSize=1000;

	//change dampening
	public static setDampening(double d){dampening=d;}
	public static Map<String,Double> computePageRank(Set<String> nodes,Map<String,Set<String>> edges){
		//initialize

		nodeSet=nodes;
		edgeMap=edges;

		//check the size of keywords and relationMap
		int n=nodeSet.size();
		int m=edgeMap.size();
		println "Keyword count: ${n}\nNumber of keywords with relationship recorded ${m}"

		//decide the order of getting items from set
		int i=0;
		nodeSet.each {
			orderMap.put(i, it);
			i++;
		}
		
		//initialize the matrix with order decided
		println "Initializing connection matrix"
		Matrix H=new Matrix(n,n);
		orderMap.each {r->
			/*decide the row number*/
			int row=r.key;
			int num=edgeMap[r.value].size();
			//initialize the weight of each cell of this row
			double val;
			if(num==0)
				val=1/n;
			else
				val=1/num;
			//r.value is the keyword, get it's link list
			edgeMap[r.value].each{c->
				/*decide the column number*/
				//set the corresponding cell in matrix
				def column=orderMap.find{key,value->value==c};
				int col=column.key;
				H.set(row, col, val);

			}
			if(row%100==0)
				println "Initialized line ${row}"

		}
		println "Initialization completed.";
		Matrix tempH=H.getMatrix(1, 10, 1, 10);
		tempH.print(10, 3);

		//calculate the pagerank
		println "Calculating pagerank";
		//		int iter = ((int) Math.abs(Math.log((double) n)
		//				/ Math.log((double) 10))) + 1;
		int iter=100;
		println "Calculate for ${iter} iterations";

		Matrix I=new Matrix(n,n,1.0);
		Matrix G;

		G=H.times(dampening).plus(I.times(1-dampening));

		Matrix tempG=G.getMatrix(1, 10, 1, 10);
		//tempG.print(20,3);

		//pi[0]=[1,1,1,...];
		Matrix pi=new Matrix(1,n,1);
		double error;
		for(int j in 0..iter){
			Matrix reg;
			println "${j} iteration:"
			reg=pi.times(G);
			//normalize pi
			reg=normalize(reg);
			//calculate difference
			Matrix diff=pi.minus(reg);
			diff=diff.arrayTimes(diff);
			//sum over the difference
			Double[][] diffArr=diff.getArray();
			double se=0;
			for(int c=0;c<n;c++){
				se+=diffArr[0][c]*diffArr[0][c];
			}
			if(se<1E-12){
				println "Squared error<1E-12. Converged already."
				error=se;
				pi=reg;
				//just for display purpose
				Matrix tempPi=pi.getMatrix(0, 0, 1, 10);
				tempPi.print(10, 3);
				break;
			}
			error=se;
			println "Squared error from last iteration is ${error}"
			//assign back to pi
			pi=reg;
			//just for display purpose
			Matrix tempPi=pi.getMatrix(0, 0, 1, 10);
			tempPi.print(10, 3);
		}

		println "Assign the final Pi";
		orderMap.each{key,value->
			rankMap.put(value, pi.get(0, key));
		}
		//sort rankMap, bigger pagerank first
		rankMap=rankMap.sort{a,b->
			b.value <=> a.value;
		}
		//find max pagerank
		println rankMap;

		return rankMap;

	}
	/*@author Eric Eaton (EricEaton@umbc.edu)
	 *         University of Maryland Baltimore County
	 * @version 0.1
	 * */
	public static Matrix normalize(Matrix m) {
		int numRows = m.getRowDimension();
		int numCols = m.getColumnDimension();

		// compute the sum of the matrix
		double sum = 0;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				sum += m.get(i, j);
			}
		}

		// normalize the matrix
		Matrix normalizedM = new Matrix(numRows, numCols);
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				normalizedM.set(i, j, m.get(i, j) / sum);
			}
		}

		return normalizedM;
	}

	public static void main(String[] args){
		def n=["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"];
		Set<String> nodes=new HashSet<String>();
		nodes.addAll(n);
		Map<String,Set<String>> edges=new HashMap<>();
		n.each{
			edges.put(it,new HashSet<String>());
		}
		edges["d"].addAll(["a"]);
		edges["b"].addAll(["c"]);
		edges["c"].addAll(["b"]);
		edges["e"].addAll(["b", "d", "f"]);
		edges["f"].addAll(["b", "e"]);
		edges["g"].addAll(["b", "e"]);
		edges["h"].addAll(["b", "e"]);
		edges["i"].addAll(["b", "e"]);
		edges["j"].addAll(["e"]);
		edges["k"].addAll(["e"]);

		computePageRank(nodes,edges);
	}

}
