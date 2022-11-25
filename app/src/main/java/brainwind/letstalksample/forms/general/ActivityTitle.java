package brainwind.letstalksample.forms.general;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import ernestoyaquello.com.verticalstepperform.Step;

public class ActivityTitle extends Step<String>
{

    private EditText titleView;

    public ActivityTitle(String title, String subtitle) {
        super(title, subtitle);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        Editable titleViewText = titleView.getText();
        return titleViewText != null ? titleViewText.toString().trim().toLowerCase() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String title = getStepData();
        return !title.isEmpty() ? title : "(Empty)";
    }

    @Override
    public void restoreStepData(String data) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        titleView.setText(data);
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        boolean isNameValid = stepData.length() >= 3;
        String errorMessage = !isNameValid ? "3 characters minimum and 70 characters max" : "";

        return new IsDataValid(isNameValid, errorMessage);
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        titleView = new EditText(getContext());
        titleView.setSingleLine(true);
        titleView.setHint(getTitle());
        titleView.setInputType(InputType.TYPE_CLASS_TEXT);

        titleView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked automatically by the form with a call to isStepDataValid().
                if(s.toString().length()>=3&s.toString().length()<=70)
                {
                    markAsCompletedOrUncompleted(true);
                }
                else
                {
                    if(s.toString().length()>70)
                    {
                        titleView.setText(titleView.getText().toString().substring(0,69));
                    }
                    markAsCompletedOrUncompleted(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return titleView;

    }

    @Override
    protected void onStepOpened(boolean animated) {
        titleView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    @Override
    protected void onStepClosed(boolean animated) {

        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(titleView.getWindowToken(),0);

    }

    public void setTitle(String title)
    {

        if(titleView!=null)
        {
            titleView.setText(title);
        }

    }


}
