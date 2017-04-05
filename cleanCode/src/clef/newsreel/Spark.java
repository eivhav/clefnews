package clef.newsreel;

import java.util.ArrayList;
/*
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
// $example on$
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
*/

/**
 * Created by havikbot on 05.04.17.
 */
public class Spark {

    public Spark(){}

    /*
    public static void runSVD(int dim, ArrayList<int[]> ratingSparseMatrix, int nbArticles){

        SparkConf conf = new SparkConf().setAppName("SVD collaborative filtering");
        SparkContext sc = new SparkContext(conf);
        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(sc);

        JavaRDD<Vector> rows = jsc.parallelize(getPcaSvdRowList(ratingSparseMatrix, nbArticles));

        // Create a RowMatrix from JavaRDD<Vector>.
        RowMatrix mat = new RowMatrix(rows.rdd());

        // Compute the top 3 singular values and corresponding singular vectors.
        SingularValueDecomposition<RowMatrix, Matrix> svd = mat.computeSVD(dim, true, 1.0E-9d);
        //RowMatrix U = svd.U();
        //Vector s = svd.s();
        Matrix V = svd.V();



        System.out.println("SVD V component is:\n");
        ArrayList<double[]> result = transformResults(V);
        for(double[] r : result){
            System.out.println(Arrays.toString(r));
        }
        System.out.println("for dims" + V.numRows() + " x " +V.numCols());

        jsc.stop();

    }


    public static void runPCA(int dim, ArrayList<int[]> ratingSparseMatrix, int nbArticles){

        SparkConf conf = new SparkConf().setAppName("PCA dim reduction");
        SparkContext sc = new SparkContext(conf);
        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(sc);


        JavaRDD<Vector> rows = jsc.parallelize(getPcaSvdRowList(ratingSparseMatrix, nbArticles));

        // Create a RowMatrix from JavaRDD<Vector>.
        RowMatrix mat = new RowMatrix(rows.rdd());

        // Compute the top 3 principal components.
        Matrix pc = mat.computePrincipalComponents(dim);
        //RowMatrix projected = mat.multiply(pc);
        System.out.println("PCA factor is:\n");
        ArrayList<double[]> result = transformResults(pc);
        for(double[] r : result){
            System.out.println(Arrays.toString(r));
        }
        System.out.println("for dims" + pc.numRows() + " x " +pc.numCols());


    }

    private static LinkedList<Vector> getPcaSvdRowList(ArrayList<int[]> ratingSparseMatrix, int nbArticles){

        double sparsity = 0;
        LinkedList<Vector> rowsList = new LinkedList<Vector>();
        for (int[] userRowIndexes : ratingSparseMatrix) {

            double[] values = new double[userRowIndexes.length];
            for(int i = 0; i < values.length; i++){ values[i] = 1.0; }

            Vector currentRow = Vectors.sparse(nbArticles, userRowIndexes, values);
            System.out.println(currentRow);
            rowsList.add(currentRow);
            sparsity += userRowIndexes.length;
            System.out.println("Sparisity now " + sparsity);
        }

        sparsity = 1 - (sparsity / (ratingSparseMatrix.size() * nbArticles));
        System.out.println("Matrix is:" + ratingSparseMatrix.size() +" x " + nbArticles);

        System.out.println("The sparsity of data = " + sparsity);

        return rowsList;

    }

    private static ArrayList<double[]> transformResults(Matrix m){
        ArrayList<double[]> resultMatrix = new ArrayList<double[]>();
        double[] arrayM = m.toArray();
        for(int r = 0; r < m.numRows(); r++){
            double[] row = new double[m.numCols()];
            for(int c = 0; c < m.numCols(); c++) {
                int index = m.numCols()*r + c;
                row[c] = arrayM[index];
            }
            resultMatrix.add(row);
        }
        return resultMatrix;


    }
    */







}
