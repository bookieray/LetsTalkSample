package brainwind.letstalksample.forms.user;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import ernestoyaquello.com.verticalstepperform.Step;

public class LastName extends Step<String>
{


    private EditText lastNameView;

    public LastName(String title, String subtitle) {
        super(title, subtitle);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        Editable userName = lastNameView.getText();
        return userName != null ? userName.toString().trim().toLowerCase() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String userName = getStepData();
        return !userName.isEmpty() ? userName : "(Empty)";
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        lastNameView.setText(stepData);
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        boolean isNameValid = stepData.length() >= 3;
        String errorMessage = !isNameValid ? "3 characters minimum" : "";

        return new IsDataValid(isNameValid, errorMessage);
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        lastNameView = new EditText(getContext());
        lastNameView.setSingleLine(true);
        lastNameView.setHint("Your Last Name");
        lastNameView.setInputType(InputType.TYPE_CLASS_TEXT);

        lastNameView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked automatically by the form with a call to isStepDataValid().
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return lastNameView;
    }

    @Override
    protected void onStepOpened(boolean animated) {
        lastNameView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    @Override
    protected void onStepClosed(boolean animated) {

        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(lastNameView.getWindowToken(),0);

    }

    public void setLastName(String lastname)
    {

        if(lastNameView!=null)
        {
            lastNameView.setText(lastname);
        }

    }
}
