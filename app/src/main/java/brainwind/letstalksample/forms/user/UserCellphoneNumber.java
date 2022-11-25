package brainwind.letstalksample.forms.user;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import ernestoyaquello.com.verticalstepperform.Step;

public class UserCellphoneNumber extends Step<String>
{

    private EditText cellphoneView;

    public UserCellphoneNumber() {
        super("Your Cellphone", "You can control access to sms notifications");
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        Editable cellphoneNumber = cellphoneView.getText();
        return cellphoneNumber != null ? cellphoneNumber.toString() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String cellphone = getStepData();
        return !cellphone.isEmpty() ? cellphone.trim().toLowerCase() : "(Empty)";
    }

    @Override
    public void restoreStepData(String data) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        cellphoneView.setText(data);
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {

        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        boolean isNameValid = stepData.length() >= 7;
        String errorMessage = !isNameValid ? "7 characters minimum" : "";
        return new IsDataValid(isNameValid, errorMessage);

    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        cellphoneView = new EditText(getContext());
        cellphoneView.setSingleLine(true);
        cellphoneView.setHint("Your Cellphone Number");
        cellphoneView.setInputType(InputType.TYPE_CLASS_PHONE);

        cellphoneView.addTextChangedListener(new TextWatcher() {

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

        return cellphoneView;

    }

    @Override
    protected void onStepOpened(boolean animated) {
        cellphoneView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    @Override
    protected void onStepClosed(boolean animated) {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(cellphoneView.getWindowToken(),0);
    }

    public void setCellphone(String cellphone)
    {

        if(cellphoneView!=null)
        {
            cellphoneView.setText(cellphone);
        }

    }



}
