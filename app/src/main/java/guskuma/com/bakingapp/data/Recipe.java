package guskuma.com.bakingapp.data;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Recipe {

    public int id;
    public String name;
    public List<Ingredient> ingredients;
    public List<Step> steps;
    public int servings;
    public String image;

}
