
package clef.newsreel;
/*
import net.librec.conf.Configuration;

import net.librec.data.model.TextDataModel;
import net.librec.filter.GenericRecommendedFilter;

import net.librec.recommender.RecommenderContext;
import net.librec.recommender.cf.ItemKNNRecommender;
import net.librec.recommender.Recommender;
import net.librec.recommender.item.RecommendedItem;

import net.librec.similarity.PCCSimilarity;
import net.librec.similarity.RecommenderSimilarity;

import net.librec.eval.RecommenderEvaluator;
import net.librec.eval.rating.RMSEEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gram on 04.04.17.
 */

public class LibRecRecommender {

    public static void main(String[] args) throws Exception {
        /*
        // build data model
        Configuration conf = new Configuration();
        conf.set("dfs.data.dir", "/home/gram/IdeaProjects/librec/data");
        TextDataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();

        // build recommender context
        RecommenderContext context = new RecommenderContext(conf, dataModel);

        // build similarity
        conf.set("rec.recommender.similarity.key" ,"item");
        RecommenderSimilarity similarity = new PCCSimilarity();
        similarity.buildSimilarityMatrix(dataModel);
        context.setSimilarity(similarity);

        // build recommender
        conf.set("rec.neighbors.knn.number", "5");
        Recommender recommender = new ItemKNNRecommender();
        recommender.setContext(context);

        // run recommender algorithm
        recommender.recommend(context);

        // evaluate the recommended result
        RecommenderEvaluator evaluator = new RMSEEvaluator();
        System.out.println("RMSE:" + recommender.evaluate(evaluator));

        // set id list of filter
        List<String> userIdList = new ArrayList();
        List<String> itemIdList = new ArrayList();
        userIdList.add("1");
        itemIdList.add("70");

        // filter the recommended result
        List<RecommendedItem> recommendedItemList = recommender.getRecommendedList();
        GenericRecommendedFilter filter = new GenericRecommendedFilter();
        filter.setUserIdList(userIdList);
        filter.setItemIdList(itemIdList);
        recommendedItemList = filter.filter(recommendedItemList);

        // print filter result
        for (RecommendedItem recommendedItem : recommendedItemList) {
            System.out.println(
                    "user:" + recommendedItem.getUserId() + " " +
                            "item:" + recommendedItem.getItemId() + " " +
                            "value:" + recommendedItem.getValue()
            );
        }
        */

    }

}

