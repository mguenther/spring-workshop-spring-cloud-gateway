package workshop.spring.gtd.api;

public class CreateItemRequest {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CreateItemRequest{" +
                "description='" + description + '\'' +
                '}';
    }
}
