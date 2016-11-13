package in.mobifirst.tagtree.addedittoken;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.util.ActivityUtilities;

public class AddEditTokenActivity extends BaseDrawerActivity {

    public static final int REQUEST_ADD_TOKEN = 1;

    @Inject
    AddEditTokenPresenter mAddEditTokensPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        AddEditTokenFragment addEditTokenFragment =
                (AddEditTokenFragment) getSupportFragmentManager().findFragmentById(R.id.content_base_drawer);

        String tokenId = null;
        if (addEditTokenFragment == null) {
            addEditTokenFragment = AddEditTokenFragment.newInstance();

//            if (getIntent().hasExtra(SettingsFragment.ARGUMENT_EDIT_TASK_ID)) {
//                tokenId = getIntent().getStringExtra(
//                        SettingsFragment.ARGUMENT_EDIT_TASK_ID);
//                actionBar.setTitle(R.string.edit_token);
//            } else {
            actionBar.setTitle(R.string.add_token);
//            }

            ActivityUtilities.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTokenFragment, R.id.content_base_drawer);
        }

        DaggerAddEditTokenComponent.builder()
                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                .addEditTokenPresenterModule(new AddEditTokenPresenterModule(addEditTokenFragment, tokenId))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}