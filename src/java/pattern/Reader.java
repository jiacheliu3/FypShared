package pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reader {

	public static String readFile(String fileName, String encoding) {
		File file = new File(fileName);
		try {
			FileInputStream inStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStream, encoding));
			String line = new String();
			String text = new String();
			while ((line = reader.readLine()) != null) {
				text += line;
			}
			reader.close();
			return text;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * �Է�����Ķ�ά�����ʽ��ȡ�ļ�
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param regex
	 *            �ļ����ڵķָ���
	 * @param encoding
	 *            ���뷽ʽ
	 * @return matrix ��ά��
	 */
	public static List<String[]> readAsMatrix(String fileName,
			String regex, String encoding) {
		List<String[]> matrix = new ArrayList<String[]>();
		File file = new File(fileName);
		try {
			FileInputStream inStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStream, encoding));
			String line = new String();
			while ((line = reader.readLine()) != null) {
				matrix.add(line.split(regex));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matrix;
	}

	public static void main(String[] args) {
		List<String[]> matrix = readAsMatrix("retail.txt", " ", "UTF-8");
		System.out.println(matrix.size());		
	}

}
