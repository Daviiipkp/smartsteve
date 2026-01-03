package implementations;

import com.daviipkp.smartsteve.Instance.Command;
import lombok.Getter;

public class SearchWebCommand extends Command {

    @Getter
    private static final SearchWebCommand instance = new SearchWebCommand();


    @Override
    public void execute() {
        if(this.isAutoCallback()) {
            callback();
        }
    }

    @Override
    public void callback() {
        if(this.isShouldUseSupCallback()) {
            this.executeSupCallback();
        }
    }

    @Override
    public void executeSupCallback() {
        //Logic before sup
        this.getSupCallback().get();
    }

    @Override
    public String getID() {
        return this.getClass().getName();
    }

    @Override
    public String getDescription(String description) {
        return "Command to search anything on Web. Usage: SEARCH_WEB_COMMAND ---SEPARATOR--- Arguments. Example: SEARCH_WEB_COMMAND ---SEPARATOR--- How to cook an egg?";
    }

}
