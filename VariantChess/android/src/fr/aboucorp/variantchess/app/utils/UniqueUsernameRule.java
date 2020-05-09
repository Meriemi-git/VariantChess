package fr.aboucorp.variantchess.app.utils;

import android.content.Context;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.Rule;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.VariantChessDatabase;

public class UniqueUsernameRule extends Rule<EditText> {


    private VariantChessDatabase db;
    private Context context;

    public UniqueUsernameRule(int sequence, VariantChessDatabase db,Context context) {
        super(sequence);
        this.db = db;
        this.context = context;
    }
    /**
     * Constructor.
     *
     * @param sequence The sequence number for this {@link Rule}.
     */
    protected UniqueUsernameRule(int sequence) {
        super(sequence);
    }

    @Override
    public boolean isValid(EditText editText) {
        return  db.userDao().findByDisplayName(editText.getText().toString()) == null;
    }


    @Override
    public String getMessage(Context context) {
        return context.getString(R.string.error_duplicate_displayname);
    }
}
