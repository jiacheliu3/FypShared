package pattern;

import java.util.ArrayList;
import java.util.List;

public class FpNode {

	String idName;// id��
	List<FpNode> children;// ���ӽ��
	FpNode parent;// �����
	FpNode next;// ��һ��id����ͬ�Ľ��
	long count;// ���ִ���

	public FpNode() {// ���ڹ������
		this.idName = null;
		this.count = -1;
		children = new ArrayList<FpNode>();
		next = null;
		parent = null;
	}

	/**
	 * ���ڹ���Ǹ���
	 * 
	 * @param idName
	 * @param count
	 */
	public FpNode(String idName) {
		this.idName = idName;
		this.count = 1;
		children = new ArrayList<FpNode>();
		next = null;
		parent = null;
	}

	/**
	 * ������ɷǸ���
	 * 
	 * @param idName
	 * @param count
	 */
	public FpNode(String idName, long count) {
		this.idName = idName;
		this.count = count;
		children = new ArrayList<FpNode>();
		next = null;
		parent = null;
	}

	/**
	 * ���һ������
	 * 
	 * @param child
	 */
	public void addChild(FpNode child) {
		children.add(child);
	}

	public void addCount(int count) {
		this.count += count;
	}

	/**
	 * ��������1
	 */
	public void addCount() {
		this.count += 1;
	}

	/**
	 * ������һ�����
	 * 
	 * @param next
	 */
	public void setNextNode(FpNode next) {
		this.next = next;
	}

	public void setParent(FpNode parent) {
		this.parent = parent;
	}

	/**
	 * ָ��ȡ����
	 * 
	 * @param index
	 * @return
	 */
	public FpNode getChilde(int index) {
		return children.get(index);
	}

	/**
	 * �����Ƿ��id��ΪidName�ĺ���
	 * 
	 * @param idName
	 * @return
	 */
	public int hasChild(String idName) {
		for (int i = 0; i < children.size(); i++)
			if (children.get(i).idName.equals(idName))
				return i;
		return -1;
	}

	public String toString() {
		return "id: " + idName + " count: " + count + " ���Ӹ��� "
				+ children.size();
	}
}
