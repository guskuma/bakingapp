package guskuma.com.bakingapp.data;

import org.parceler.Parcel;

import java.text.DecimalFormat;
import java.util.List;

@Parcel
public class Recipe {

    public int id;
    public String name;
    public List<Ingredient> ingredients;
    public List<Step> steps;
    public int servings;
    public String image;

    public String getIngredientsText(){
        String returnText =  "";
        final DecimalFormat format = new DecimalFormat("0.#");
        for (Ingredient i : ingredients) {
            String quantity = format.format(i.quantity);
            String measure = handleMeasure(i.measure);
            returnText += quantity + measure + i.ingredient + "; ";
        }

        return returnText;
    }

    private String handleMeasure(String measure) {
        if("UNIT".equals(measure)) {
            return " ";
        }

        if("CUP".equals(measure) || "TSP".equals(measure) || "TBLSP".equals(measure))
            return " " + measure.toLowerCase() + " ";

        return measure.toLowerCase() + " ";
    }

    @Override
    public String toString() {
        return name;
    }
}
