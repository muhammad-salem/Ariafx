package ariafx.core.url.type;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ariafx.opt.R;
import ariafx.opt.Utils;

public class Category extends Type{

	
    String saveTo;
    String format;
    

    public static final Category Default = new Category("Default");
    public static final Category Application
            = new Category("Application", "exe msi deb rpm");
    public static final Category Compressed
            = new Category("Compressed", "zip zip iso rar r0* r1* arj sit"
                    + " sitxsea ace bz2 7z gz tar");
    public static final Category Docouments
            = new Category("Docouments", "doc pdf ppt pps");
    public static final Category Music
            = new Category("Music", "mp3 wav wma "
                    + "mpa ram ra aac aif "
                    + "m4a ogg");
    public static final Category Video
            = new Category("Video", "avi mpg mp4 mpeg "
                    + "asf wmv mov qt rm "
                    + "mp4 flv m4v webm ogv ogg mkv");
    private static final Category Picture
            = new Category("Picture", "png jpg jpeg gif");

    public static ObservableList<Category> categores 
    		= FXCollections.observableArrayList( Default,
            Application, Compressed,Docouments, Music, Video, Picture);
    
    public static ObservableList<Category> newCategores = FXCollections.observableArrayList();
    
   
    
    public static Category get(int index) {
		return categores.get(index);
	}
	public static int indexOf(Object o) {
		return categores.indexOf(o);
	}
	public Category() {
	    	id = 1;
	    }
    public Category(String name) {
    	this();
        this.name = name;
        this.saveTo = R.DefaultPath;
        format = "";
    }

    public Category(String name, String extensionFilters) {
    	this();
        this.name = name;
        this.saveTo = R.DefaultPath + File.separator + name;
        format = extensionFilters;
    }
    
    public Category(int id, String name, String extensionFilters) {
    	this();
        this.name = name;
        this.saveTo = R.DefaultPath + File.separator + name;
        format = extensionFilters;
    }

    public Category(String name, String dir, String extensionFilters) {
    	this();
        this.name = name;
        this.saveTo = dir;
        format = extensionFilters;
    }

    

    public ObservableList<String> getCategoryItems() {
        ObservableList<String> cat = FXCollections.observableArrayList();
        for (Category element : categores) {
            cat.add(element.name);
        }
        return cat;
    }

    /**
     * search category by format Extension
     * @param format
     * @return
     */
    public static Category get(String format) {
        for (Category c : categores) {
            if (format.equals(c.name)) {
                return c;
            }
        }
        for (Category c : newCategores) {
            if (format.equals(c.name)) {
                return c;
            }
        }
        return Default;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getSaveTo() {
        return saveTo;
    }

    public void setSaveTo(String saveTo) {
        this.saveTo = saveTo;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /*
     * ==========================================================================
     */
    public static Category getCategoryByExtension(String extension) {
        for (Category c : categores) {
            if (c.format.contains(extension)) {
                return c;
            }
        }
        for (Category c : newCategores) {
            if (c.format.contains(extension)) {
                return c;
            }
        }
        return Default;
    }

    public static ObservableList<Category> getCategores() {
    	ObservableList<Category> cat = FXCollections.observableArrayList();
    	for (Category c : categores) {
           cat.add(c);
        }
        for (Category c : newCategores) {
        	cat.add(c);
        }
        return cat;
    }

    public static void setCategores(ObservableList<Category> categores) {
        newCategores = categores;
    }

    public static boolean contains(Category o) {
        return categores.contains(o);
    }
    
    public static boolean containsNew(Category o) {
        return newCategores.contains(o);
    }

    public static boolean add(Category e) {
        if (contains(e)) {
            return false;
        }
        if (containsNew(e)) {
            return false;
        }
        return newCategores.add(e);
    }

    public static boolean remove(Object o) {
        return categores.remove(o);
    }
    
    /**
     * get the default categories
     * @return
     */
	public static Category[] getCategoriesArray() {
		Category[] cat = new Category[categores.size()];
		for (int i = 0; i < cat.length; i++) {
			cat[i] = categores.get(i);
		}
		return cat;
	}
	
	/**
     * get only the new categories
     * @return
     */
	public static Category[] getNewCatArray() {
		Category[] cat = new Category[newCategores.size()];
		cat= newCategores.toArray(cat);
		return cat;
	}
	
	public String asToString() {
		return  "name:" + name + " id=" + id +"\n" + saveTo +"\n" +format;
	}
	
	/**------------------------------SAVE OPT-----------------------------------**/
	
	/**
	 * save only the new categories
     * @return void 
	 */
	
	public static void saveNewCategories() {
		for (Category category : newCategores) {
			category.toJson();
		}
	}
	
	public void toJson() {
		toJson(R.OptCategoresDir+ File.separator + name + ".json");
	}
	public void toJson(String file) {
		Utils.toJsonFile(file, this);
	}
	
	public static Category fromJson(String file) {
		return Utils.fromJson(file, Category.class);
	}
	
}
