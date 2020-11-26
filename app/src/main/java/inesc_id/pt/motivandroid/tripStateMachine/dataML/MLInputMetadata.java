package inesc_id.pt.motivandroid.tripStateMachine.dataML;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import inesc_id.pt.motivandroid.tripStateMachine.SortMapByValue;

/**
 *  MLInputMetadata
 *
 * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
 * developed in the context of the European research project MoTiV (motivproject.eu). The
 * code was developed by partner INESC-ID with contributions in graphics design by partner
 * TIS. The Woorti app development was one of the outcomes of a Work Package of the MoTiV
 * project.
 * The Woorti app was originally intended as a tool to support data collection regarding
 * mobility patterns from city and country-wide campaigns and provide the data and user
 * management to campaign managers.
 *
 * The Woorti app development followed an agile approach taking into account ongoing
 * feedback of partners and testing users while continuing under development. This has
 * been carried out as an iterative process deploying new app versions. Along the
 * timeline, various previously unforeseen requirements were identified, some requirements
 * Were revised, there were requests for modifications, extensions, or new aspects in
 * functionality or interaction as found useful or interesting to campaign managers and
 * other project partners. Most stemmed naturally from the very usage and ongoing testing
 * of the Woorti app. Hence, code and data structures were successively revised in a
 * way not only to accommodate this but, also importantly, to maintain compatibility with
 * the functionality, data and data structures of previous versions of the app, as new
 * version roll-out was never done from scratch.
 * The code developed for the Woorti app is made available as open source, namely to
 * contribute to further research in the area of the MoTiV project, and the app also makes
 * use of open source components as detailed in the Woorti app license.
 * This project has received funding from the European Union’s Horizon 2020 research and
 * innovation programme under grant agreement No. 770145.
 * This file is part of the Woorti app referred to as SOFTWARE.
 */
public class MLInputMetadata implements Serializable{

    MLAlgorithmInput mlAlgorithmInput;

    HashMap<Integer, Double> probasDicts;
    List<KeyValueWrapper> probasOrdered;

    public MLInputMetadata(MLAlgorithmInput mlAlgorithmInput, HashMap<Integer, Double> probasDicts){
        this.mlAlgorithmInput = mlAlgorithmInput;
        this.probasDicts = probasDicts;
        LinkedHashMap<Integer, Double> orderedRes = (LinkedHashMap<Integer, Double>) SortMapByValue.sortByValueDesc(probasDicts);
        List<Map.Entry<Integer, Double>> temp = new ArrayList<>(orderedRes.entrySet());

        probasOrdered = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : temp){
            probasOrdered.add(new KeyValueWrapper(entry.getKey(), entry.getValue()));
        }


    }

    public MLInputMetadata(){}

    public Double getProbabilityByMode(int mode){
        return  probasDicts.get(mode);
    }

    public MLAlgorithmInput getMlAlgorithmInput() {
        return mlAlgorithmInput;
    }

    public void setMlAlgorithmInput(MLAlgorithmInput mlAlgorithmInput) {
        this.mlAlgorithmInput = mlAlgorithmInput;
    }

    public HashMap<Integer, Double> getProbasDicts() {
        return probasDicts;
    }

    public void setProbasDicts(HashMap<Integer, Double> probasDicts) {
        this.probasDicts = probasDicts;
    }

    public List<KeyValueWrapper> getProbasOrdered() {
        return probasOrdered;
    }

    public void setProbasOrdered(List<KeyValueWrapper> probasOrdered) {
        this.probasOrdered = probasOrdered;
    }

    public KeyValueWrapper getBestMode(){
        return probasOrdered.get(0);
    }

}


//public class MLInputMetadata implements Serializable{
//
//    MLAlgorithmInput mlAlgorithmInput;
//
//    HashMap<Integer, Double> probasDicts;
//    List<Map.Entry<Integer, Double>> probasOrdered;
//
//    public MLInputMetadata(MLAlgorithmInput mlAlgorithmInput, HashMap<Integer, Double> probasDicts){
//        this.mlAlgorithmInput = mlAlgorithmInput;
//        this.probasDicts = probasDicts;
//        LinkedHashMap<Integer, Double> orderedRes = (LinkedHashMap<Integer, Double>) SortMapByValue.sortByValueDesc(probasDicts);
//        this.probasOrdered = new ArrayList<>(orderedRes.entrySet());
//    }
//
//    public MLInputMetadata(){}
//
//    public Double getProbabilityByMode(int mode){
//        return  probasDicts.get(mode);
//    }
//
//    public MLAlgorithmInput getMlAlgorithmInput() {
//        return mlAlgorithmInput;
//    }
//
//    public void setMlAlgorithmInput(MLAlgorithmInput mlAlgorithmInput) {
//        this.mlAlgorithmInput = mlAlgorithmInput;
//    }
//
//    public HashMap<Integer, Double> getProbasDicts() {
//        return probasDicts;
//    }
//
//    public void setProbasDicts(HashMap<Integer, Double> probasDicts) {
//        this.probasDicts = probasDicts;
//    }
//
//    public List<Map.Entry<Integer, Double>> getProbasOrdered() {
//        return probasOrdered;
//    }
//
//    public void setProbasOrdered(List<Map.Entry<Integer, Double>> probasOrdered) {
//        this.probasOrdered = probasOrdered;
//    }
//
//    public Map.Entry<Integer, Double> getBestMode(){
//        return probasOrdered.get(0);
//    }
//
//}
