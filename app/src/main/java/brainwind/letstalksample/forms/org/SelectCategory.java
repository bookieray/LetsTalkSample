package brainwind.letstalksample.forms.org;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;

import brainwind.letstalksample.R;
import ernestoyaquello.com.verticalstepperform.Step;


public class SelectCategory extends Step<String>
{

    Spinner spinner;
    Context context;

    public SelectCategory(Context context) {
        super("Select Category", "Please select correct category");
        this.context=context;
    }

    @Override
    public String getStepData() {
        return spinner != null ? spinner.getSelectedItem().toString().trim().toLowerCase() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return spinner != null ? spinner.getSelectedItem().toString() : "";
    }

    @Override
    public void restoreStepData(String data) {

        if(spinner!=null)
        {

            for(int i=0;i<spinner.getCount();i++)
            {

                String item=spinner.getItemAtPosition(getPosition()).toString();

                if(item.trim().equals(data.trim()))
                {
                    spinner.setSelection(i);
                }

            }

        }

    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        String setpdata=getStepData();
        String errorMessage = setpdata.trim().isEmpty() ? "Please select category" : "";
        return new IsDataValid(setpdata.trim().isEmpty()==false, errorMessage);
    }

    @Override
    protected View createStepContentLayout() {
        View view= LayoutInflater.from(context).inflate(R.layout.form_select_org_type,
                null,false);
        spinner=(Spinner) view.findViewById(R.id.spinner);
        return view;
    }

    @Override
    protected void onStepOpened(boolean animated) {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(spinner.getWindowToken(),0);
    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    public void setCategory(String category)
    {

        if(spinner!=null)
        {

            for(int i=0;i<spinner.getCount();i++)
            {

                String item=spinner.getItemAtPosition(getPosition()).toString();

                if(item.trim().equals(category.trim()))
                {
                    spinner.setSelection(i);
                }

            }

        }

    }

}
