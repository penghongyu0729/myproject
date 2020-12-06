package ex17;
//Date in 2020.12.7 0:50
public class MySort {
	public static void main(String args[])
	{
		int num=10;//size is too small
		int []a=new int[num];
		for(int i=0;i<num;i++)
		{
			a[i]=10-i;
		}
		sort(a, num);
		for(int i=0;i<num;i++)
		{
			System.out.print(i+" ");
		}
	}
	//this is buttle sort
	public static void sort(int []a,int num)
	{
		int tmp;
		for(int i=0;i<num-1;i++)
		{
			for(int j=0;j<num-1-i;j++)
			{
				if(a[j]>a[j+1])
				{
					tmp=a[j];
					a[j]=a[j+1];
					a[j+1]=tmp;
				}
			}
		}
	}
}
