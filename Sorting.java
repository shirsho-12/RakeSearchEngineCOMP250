package finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry; // You may need it to implement fastSort

public class Sorting {

	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n^2) as it uses bubble sort, where n is the number 
	 * of pairs in the map. 
	 */
    public static <K, V extends Comparable> ArrayList<K> slowSort (HashMap<K, V> results) {
        ArrayList<K> sortedUrls = new ArrayList<K>();
        sortedUrls.addAll(results.keySet());	//Start with unsorted list of urls

        int N = sortedUrls.size();
        for(int i=0; i<N-1; i++){
			for(int j=0; j<N-i-1; j++){
				if(results.get(sortedUrls.get(j)).compareTo(results.get(sortedUrls.get(j+1))) <0){
					K temp = sortedUrls.get(j);
					sortedUrls.set(j, sortedUrls.get(j+1));
					sortedUrls.set(j+1, temp);
				}
			}
        }
        return sortedUrls;                    
    }
    
    
	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n*log(n)), where n is the number 
	 * of pairs in the map. 
	 */
    public static <K, V extends Comparable> ArrayList<K> fastSort(HashMap<K, V> results) {
    	// ADD YOUR CODE HERE
		ArrayList<K> sortedUrls = new ArrayList<K>();
		sortedUrls.addAll(results.keySet());	//Start with unsorted list of urls

//		Sorting.quicksort(shuffle(sortedUrls), results, 0, sortedUrls.size() - 1);
		Sorting.mergeSort(sortedUrls, results, 0, sortedUrls.size() - 1);
    	return sortedUrls;
    }
//	 private static <K, V extends Comparable> int partition(ArrayList<K> arr, HashMap<K, V> results, int low, int high){
//		V pivot = results.get(arr.get(high));
//		int i = low - 1;
//		for (int j = low; j < high; j++){
//			if (results.get(arr.get(j)).compareTo(pivot) >= 0){
//				i++;
//				K temp = arr.get(i);
//				arr.set(i, arr.get(j));
//				arr.set(j, temp);
//			}
//		}
//		K temp = arr.get(i+1);
//		arr.set(i+1, arr.get(high));
//		arr.set(high, temp);
//		return (i+1);
//	 }

//	 private static <K, V extends Comparable> void quicksort(ArrayList<K> arr, HashMap<K, V> results, int low, int high){
//		if (low < high){
//			int idx = partition(arr, results, low, high);
//			quicksort(arr, results, low, idx - 1);
//			quicksort(arr, results, idx + 1, high);
//		}
//
//	 }

//     private static <K, V extends Comparable> ArrayList<K> shuffle(ArrayList<K> array){
//
//    	java.util.Random rand = new java.util.Random();
//    	rand.setSeed(123456789);
//
//		for (int i = 0; i < array.size(); i++) {
//			int randomIndexToSwap = rand.nextInt(array.size());
//			K temp = array.get(randomIndexToSwap);
//			array.set(randomIndexToSwap, array.get(i));
//			array.set(i, temp);
//		}
//		return array;
//     }

	private static <K, V extends Comparable> void merge(ArrayList<K> arr, HashMap<K, V> results, int left, int mid, int right){
    	ArrayList<K> temps = new ArrayList<>();
    	int i = left, j = mid + 1;
    	while (i <= mid || j <= right){
    		if (i > mid){
    			temps.add(arr.get(j));
    			j++;
			}
    		else if (j > right){
				temps.add(arr.get(i));
				i++;
			}
    		else if (results.get(arr.get(i)).compareTo(results.get(arr.get(j))) > 0){
    			temps.add(arr.get(i));
    			i++;
			}
    		else {
    			temps.add(arr.get(j));
    			j++;
			}
		}
    	for (int k = left; k <= right; k++){
    		arr.set(k, temps.get(k - left));
		}
	}

    private static <K, V extends Comparable> void mergeSort(ArrayList<K> arr, HashMap<K, V> results, int left, int right){
    	if (left < right){
    		int mid = (left + right) / 2;
			mergeSort(arr, results, left, mid);
			mergeSort(arr, results, mid+1, right);
			merge(arr, results, left, mid, right);
    	}
    }
}