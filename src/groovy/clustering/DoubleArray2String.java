package clustering;

public class DoubleArray2String {

	public static String[][] convert(double[] arr){
		String[][] trainingdata = new String[][] {{ "1.0,0.5,1,1,1,1", "1,1,1,1,1,1", "1,1,1,1,1,1",
		"1,1,1,1,1,1" }, {" "} };
		
		System.out.println(trainingdata[1].toString());
		
//		String[][][] td = new String[][][] {
//				{
//						{ "1,0,1,1,1,1", "1,1,1,1,1,1", "1,1,1,1,1,1",
//								"1,1,1,1,1,1" }, { "data_bmp" } },
//				{ { "0,0,0,0,0,0", "0,0,0,0,0,0", "0,0,0,0,0,0" },
//						{ "data_jpeg" } },
//				{ { "1,1,1,1,1,1", "1,1,1,1,1,0", "1,1,1,0,1,0" },
//						{ "data_gif" } } };
//		
		String[][] result=new String[2][1];
		String m=arr[0]+"";
		for(int i=1;i<arr.length;i++){
			m+=","+arr[i];
		}
		result[0]=new String[]{m};
		result[1]=new String[]{""};
		
		
		return result;
	}
	public static String[][][] convert(double[][] arr){
		int groupCount=0;
		for(double[] line:arr){
			groupCount++;
		}
		System.out.println(groupCount+" groups found");
		String[][][] results=new String[groupCount][2][1];
		int index=0;
		for(double[] line:arr){
			//System.out.println(line);
			String[][] part=convert(line);
			results[index]=part;
			index++;
		}
		
		return results;
	}
	public static void main(String[] args){
		double[] arr={1.0,1.0};
		convert(arr);
	}
}