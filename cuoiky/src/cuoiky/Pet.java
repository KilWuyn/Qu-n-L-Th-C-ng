package cuoiky;

public class Pet{
	private String name;
    private String owner;
    private String species;
    private String sex;
    private String birth;
    private String death;
    
    public Pet() {}
    
    public Pet(String name, String owner, String species, String sex, String birth, String death) {
        this.name = name;
        this.owner = owner;
        this.species = species;
        this.sex = sex;
        this.birth = birth;
        this.death = death;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
    
    public String getBirth() {
    	return birth;
    }
    
    public void setBirth(String birth) {
    	this.birth = birth;
    }
    
    public String getDeath() {
    	return death;
    }
    
    public void setDeath(String death) {
    	this.death = death;
    }
    
    public String toString() {
        return "Pet{" +
               "name='" + name + '\'' +
               ", owner='" + owner + '\'' +
               ", species='" + species + '\'' +
               ", sex='" + sex + '\'' +
               ", birth='" + birth + '\'' +
               ", death='" + death + '\'' +
               '}';
    }
}
