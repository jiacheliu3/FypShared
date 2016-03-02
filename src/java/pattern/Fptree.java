package pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Fptree {
	private int id;
	private static final float SUPPORT = 0.3f;
	private static long absSupport;
	private String path;
	public static String basePath="D:/pattern/";
	public static void main(String[] args) {
		Fptree tree=new Fptree(1);
		List<String[]> matrix = Reader.readAsMatrix("D:/pattern/100.txt", "\t", "utf-8");
		absSupport = (long) (SUPPORT * matrix.size());
		//absSupport = (long) (0.05 * matrix.size());
		System.out.println("Absolute support: " + absSupport);
		System.out.println("Patterns:");
		Map<String, Integer> frequentMap = new LinkedHashMap<String, Integer>();// һ��Ƶ����
		Map<String, FpNode> header = tree.getHeader(matrix, frequentMap);
		FpNode root = tree.getFpTree(matrix, header, frequentMap);
		// printTree(root);
		Map<Set<FpNode>, Long> frequents = tree.fpGrowth(root, header, null);
		for (Map.Entry<Set<FpNode>, Long> fre : frequents.entrySet()) {
			for (FpNode node : fre.getKey())
				System.out.print(node.idName + " ");
			System.out.println("\t" + fre.getValue());
		}
	}

	public Fptree(int number){
		id=number;
		path=basePath+"${id}input.txt";
	}
	public void setThreshold(long limit){
		absSupport=limit;
		System.out.println("Minimum support set to "+limit);
	}
	public void setPath(String p){
		path=p;
		System.out.println("Path set to "+path);
	}
	public Map mine(String path){
		List<String[]> matrix = Reader.readAsMatrix(path, "\t", "utf-8");
		if(absSupport==0L){
			System.out.println("Minimum support not set, now setting it to 0.3 of size.");
			absSupport = (long) (SUPPORT * matrix.size());
		}
		
		System.out.println("Absolute support: " + absSupport);
		System.out.println("Patterns:");
		Map<String, Integer> frequentMap = new LinkedHashMap<String, Integer>();// һ��Ƶ����
		Map<String, FpNode> header = getHeader(matrix, frequentMap);
		FpNode root = getFpTree(matrix, header, frequentMap);
		// printTree(root);
		Map<Set<FpNode>, Long> frequents = fpGrowth(root, header, null);
		
		if(frequents==null){
			System.out.println("No frequent items found. ");
			return null;
		}
		//convert FpNode to a set of string
		Map<Set<String>,Long> patterns=new HashMap<Set<String>,Long>();
		for (Map.Entry<Set<FpNode>, Long> fre : frequents.entrySet()) {
			Set<String> names=new HashSet<String>();
			for (FpNode node : fre.getKey()){
				names.add(node.idName);
			}
			patterns.put(names, fre.getValue());
		}
		
		return patterns;
	}
	/**
	 * ��fp�����ݹ���ƽ����
	 * 
	 * @param root
	 * @param header
	 */
	private Map<Set<FpNode>, Long> fpGrowth(FpNode root,
			Map<String, FpNode> header, String idName) {
		Map<Set<FpNode>, Long> conditionFres = new HashMap<Set<FpNode>, Long>();
		Set<String> keys = header.keySet();
		String[] keysArray = keys.toArray(new String[0]);
		if(keysArray.length==0){
			System.out.println("No freqent items for FP tree study.");
			return null;
		}
		String firstIdName = keysArray[keysArray.length - 1];
		if (isSinglePath(header, firstIdName)) {// ֻ��һ��·��ʱ����·���ϵ�������ϼ��ɵõ�����Ƶ����
			if (idName == null)
				return conditionFres;
			FpNode leaf = header.get(firstIdName);
			List<FpNode> paths = new ArrayList<FpNode>();// �Զ����ϱ���·�����
			paths.add(leaf);
			FpNode node = leaf;
			while (node.parent.idName != null) {
				paths.add(node.parent);
				node = node.parent;
			}
			conditionFres = getCombinationPattern(paths, idName);
			FpNode tempNode = new FpNode(idName, -1L);
			conditionFres = addLeafToFrequent(tempNode, conditionFres);

		} else {
			for (int i = keysArray.length - 1; i >= 0; i--) {// �ݹ�����������Ƶ����
				String key = keysArray[i];
				List<FpNode> leafs = new ArrayList<FpNode>();
				FpNode link = header.get(key);
				while (link != null) {
					leafs.add(link);
					link = link.next;
				}
				Map<List<String>, Long> paths = new HashMap<List<String>, Long>();
				Long leafCount = 0L;
				FpNode noParentNode = null;
				for (FpNode leaf : leafs) {
					List<String> path = new ArrayList<String>();
					FpNode node = leaf;
					while (node.parent.idName != null) {
						path.add(node.parent.idName);
						node = node.parent;
					}
					leafCount += leaf.count;
					if (path.size() > 0)
						paths.put(path, leaf.count);
					else {// û�и����
						noParentNode = leaf;
					}
				}
				if (noParentNode != null) {
					Set<FpNode> oneItem = new HashSet<FpNode>();
					oneItem.add(noParentNode);
					if (idName != null)
						oneItem.add(new FpNode(idName, -2));
					conditionFres.put(oneItem, leafCount);
				}
				Holder holder = getConditionFpTree(paths);
				if (holder.header.size() != 0) {
					// if (idName != null)
					// key = idName + " " + key;
					Map<Set<FpNode>, Long> preFres = fpGrowth(holder.root,
							holder.header, key);
					if (idName != null) {
						FpNode tempNode = new FpNode(idName, leafCount);
						preFres = addLeafToFrequent(tempNode, preFres);
					}
					conditionFres.putAll(preFres);
				}
			}
		}
		return conditionFres;

	}

	/**
	 * ��Ҷ�ӽ����ӵ�Ƶ������
	 * 
	 * @param leaf
	 * @param conditionFres
	 */
	private Map<Set<FpNode>, Long> addLeafToFrequent(FpNode leaf,
			Map<Set<FpNode>, Long> conditionFres) {
		if (conditionFres.size() == 0) {
			Set<FpNode> set = new HashSet<FpNode>();
			set.add(leaf);
			conditionFres.put(set, leaf.count);
		} else {
			Set<Set<FpNode>> keys = new HashSet<Set<FpNode>>(
					conditionFres.keySet());
			for (Set<FpNode> set : keys) {
				Long count = conditionFres.get(set);
				conditionFres.remove(set);
				set.add(leaf);
				conditionFres.put(set, count);
			}
		}
		return conditionFres;
	}

	/**
	 * �ж�һ��fptree�Ƿ�Ϊ��һ·��
	 * 
	 * @param header
	 * @param tableLink
	 * @return
	 */
	private boolean isSinglePath(Map<String, FpNode> header,
			String tableLink) {
		if (header.size() == 1 && header.get(tableLink).next == null)
			return true;
		return false;
	}

	/**
	 * ���������
	 * 
	 * @param paths
	 * @return
	 */
	private Holder getConditionFpTree(Map<List<String>, Long> paths) {
		List<String[]> matrix = new ArrayList<String[]>();
		for (Map.Entry<List<String>, Long> entry : paths.entrySet()) {
			for (long i = 0; i < entry.getValue(); i++) {
				matrix.add(entry.getKey().toArray(new String[0]));
			}
		}
		Map<String, Integer> frequentMap = new LinkedHashMap<String, Integer>();// һ��Ƶ����
		Map<String, FpNode> cHeader = getHeader(matrix, frequentMap);
		FpNode cRoot = getFpTree(matrix, cHeader, frequentMap);
		return new Holder(cRoot, cHeader);
	}

	/**
	 * ��һ·���ϵ�������ϼ���idName���ɵ�Ƶ����
	 * 
	 * @param paths
	 * @param idName
	 * @return
	 */
	private Map<Set<FpNode>, Long> getCombinationPattern(
			List<FpNode> paths, String idName) {
		Map<Set<FpNode>, Long> conditionFres = new HashMap<Set<FpNode>, Long>();
		int size = paths.size();
		for (int mask = 1; mask < (1 << size); mask++) {// ��������ϣ���1��ʼ��ʾ���Կռ�
			Set<FpNode> set = new HashSet<FpNode>();
			// �ҳ�ÿ�ο��ܵ�ѡ��
			for (int i = 0; i < paths.size(); i++) {
				if ((mask & (1 << i)) > 0) {
					set.add(paths.get(i));
				}
			}
			long minValue = Long.MAX_VALUE;
			for (FpNode node : set) {
				if (node.count < minValue)
					minValue = node.count;
			}
			conditionFres.put(set, minValue);
		}
		return conditionFres;
	}

	/**
	 * ��ӡfp��
	 * 
	 * @param root
	 */
	private void printTree(FpNode root) {
		System.out.println(root);
		FpNode node = root.getChilde(0);
		System.out.println(node);
		for (FpNode child : node.children)
			System.out.println(child);
		System.out.println("*****");
		node = root.getChilde(1);
		System.out.println(node);
		for (FpNode child : node.children)
			System.out.println(child);

	}

	/**
	 * ����FP��,ͬʱ���÷����ĸ����ø��±�ͷ
	 * 
	 * @param matrix
	 * @param header
	 * @param frequentMap
	 * @return ������ĸ���
	 */
	private FpNode getFpTree(List<String[]> matrix,
			Map<String, FpNode> header, Map<String, Integer> frequentMap) {
		FpNode root = new FpNode();
		int count = 0;
		for (String[] line : matrix) {
			String[] orderLine = getOrderLine(line, frequentMap);
//			count++;
//			if (count % 100000 == 0)
//				System.out.println(count);
			FpNode parent = root;
			for (String idName : orderLine) {
				int index = parent.hasChild(idName);
				if (index != -1) {// �Ѿ����˸�id������Ҫ�½����
					parent = parent.getChilde(index);
					parent.addCount();
				} else {
					FpNode node = new FpNode(idName);
					parent.addChild(node);
					node.setParent(parent);
					FpNode nextNode = header.get(idName);
					if (nextNode == null) {// ��ͷ���ǿյģ���ӵ���ͷ
						header.put(idName, node);
					} else {// ��ӵĽ������
						while (nextNode.next != null) {
							nextNode = nextNode.next;
						}
						nextNode.next = node;
					}
					parent = node;// �Ժ�Ľ����ڵ�ǰ�������
				}
			}
		}
		return root;
	}

	/**
	 * ��line������id����frequentMap��ֵ�ý�������
	 * 
	 * @param line
	 * @param frequentMap
	 * @return
	 */
	private String[] getOrderLine(String[] line,
			Map<String, Integer> frequentMap) {
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		for (String idName : line) {
			if (frequentMap.containsKey(idName)) {// ���˵���һ��Ƶ����
				countMap.put(idName, frequentMap.get(idName));
			}
		}
		List<Map.Entry<String, Integer>> mapList = new ArrayList<Map.Entry<String, Integer>>(
				countMap.entrySet());
		Collections.sort(mapList, new Comparator<Map.Entry<String, Integer>>() {// ��������
					@Override
					public int compare(Entry<String, Integer> v1,
							Entry<String, Integer> v2) {
						return v2.getValue() - v1.getValue();
					}
				});
		String[] orderLine = new String[countMap.size()];
		int i = 0;
		for (Map.Entry<String, Integer> entry : mapList) {
			orderLine[i] = entry.getKey();
			i++;
		}
		return orderLine;
	}

	/**
	 * ��ɱ�ͷ
	 * 
	 * @param matrix
	 *            �����¼
	 * @return header ��ͷ�ļ�Ϊid�ţ����Ұ��ճ��ִ���Ľ�������
	 */
	private Map<String, FpNode> getHeader(List<String[]> matrix,
			Map<String, Integer> frequentMap) {
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		for (String[] line : matrix) {
			for (String idName : line) {
				if (countMap.containsKey(idName)) {
					countMap.put(idName, countMap.get(idName) + 1);
				} else {
					countMap.put(idName, 1);
				}
			}
		}
		for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
			if (entry.getValue() >= absSupport)// ���˵�������֧�ֶȵ���
				frequentMap.put(entry.getKey(), entry.getValue());
		}
		List<Map.Entry<String, Integer>> mapList = new ArrayList<Map.Entry<String, Integer>>(
				frequentMap.entrySet());
		Collections.sort(mapList, new Comparator<Map.Entry<String, Integer>>() {// ��������
					@Override
					public int compare(Entry<String, Integer> v1,
							Entry<String, Integer> v2) {
						return v2.getValue() - v1.getValue();
					}
				});
		frequentMap.clear();// ��գ��Ա㱣������ļ�ֵ��
		Map<String, FpNode> header = new LinkedHashMap<String, FpNode>();
		for (Map.Entry<String, Integer> entry : mapList) {
			header.put(entry.getKey(), null);
			frequentMap.put(entry.getKey(), entry.getValue());
		}
		return header;
	}
}

/**
 * 
 * ����������õ��İ�װ��
 * 
 */
class Holder {
	public final FpNode root;
	public final Map<String, FpNode> header;

	public Holder(FpNode root, Map<String, FpNode> header) {
		this.root = root;
		this.header = header;
	}
}