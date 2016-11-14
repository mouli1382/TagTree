package in.mobifirst.tagtree.tokens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.addedittoken.AddEditTokenActivity;
import in.mobifirst.tagtree.model.Token;

public class SnapFragment extends Fragment implements TokensContract.View {

    private TokensContract.Presenter mPresenter;

    private SnapAdapter mSnapAdapter;

    private View mNoTokensView;

    private ImageView mNoTokenIcon;

    private TextView mNoTokenMainView;

    private TextView mNoTokenAddView;

    private LinearLayout mTokensView;

    private TextView mFilteringLabelView;

    public SnapFragment() {
        // Requires empty public constructor
    }

    public static SnapFragment newInstance() {
        return new SnapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSnapAdapter = new SnapAdapter();
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull TokensContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewToken();
            }
        });
        fab.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_issuetokens, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mSnapAdapter);

        mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
        mTokensView = (LinearLayout) root.findViewById(R.id.tokensLL);

        // Set up  no Tokens view
        mNoTokensView = root.findViewById(R.id.notokens);
        mNoTokenIcon = (ImageView) root.findViewById(R.id.notokensIcon);
        mNoTokenMainView = (TextView) root.findViewById(R.id.notokensMain);
        mNoTokenAddView = (TextView) root.findViewById(R.id.notokensAdd);
        mNoTokenAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToken();
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTokensMap(false);
            }
        });

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mPresenter.clearCompletedTokens();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mPresenter.loadTokensMap(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tokens_fragment_menu, menu);
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tokens, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.active:
                        mPresenter.setFiltering(TokensFilterType.ACTIVE_TOKENS);
                        break;
                    case R.id.completed:
                        mPresenter.setFiltering(TokensFilterType.COMPLETED_TOKENS);
                        break;
                    case R.id.cancelled:
                        mPresenter.setFiltering(TokensFilterType.CANCELLED_TOKENS);
                        break;
                    default:
                        mPresenter.setFiltering(TokensFilterType.ALL_TOKENS);
                        break;
                }
                mPresenter.loadTokensMap(false);
                return true;
            }
        });

        popup.show();
    }

    /**
     * Listener for clicks on Tokens in the ListView.
     */
    TokenItemListener mItemListener = new TokenItemListener() {
        @Override
        public void onTokenClick(Token clickedToken) {
            mPresenter.openTokenDetails(clickedToken);
        }

        @Override
        public void onCompleteTokenClick(Token completedToken) {
            mPresenter.completeToken(completedToken);
        }

        @Override
        public void onActivateTokenClick(Token activatedToken) {
            mPresenter.activateToken(activatedToken);
        }

        @Override
        public void onCancelTokenClick(Token cancelledToken) {
            mPresenter.cancelToken(cancelledToken);
        }
    };

    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTokens(List<Token> Tokens) {
    }

    @Override
    public void showTokens(Map<Integer, Collection<Token>> tokenMap) {
        mSnapAdapter.replaceData(tokenMap);
        mTokensView.setVisibility(View.VISIBLE);
        mNoTokensView.setVisibility(View.GONE);
    }

    @Override
    public void showNoActiveTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_active),
                R.drawable.ic_check_circle_24dp,
                false
        );
    }

    @Override
    public void showNoTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showNoCompletedTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showNoCancelledTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_token_message));
    }

    private void showNoTokensViews(String mainText, int iconRes, boolean showAddView) {
        mTokensView.setVisibility(View.GONE);
        mNoTokensView.setVisibility(View.VISIBLE);

        mNoTokenMainView.setText(mainText);
        mNoTokenIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTokenAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_active));
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void showCancelledFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
    }

    @Override
    public void showAddToken() {
        Intent intent = new Intent(getContext(), AddEditTokenActivity.class);
        startActivityForResult(intent, AddEditTokenActivity.REQUEST_ADD_TOKEN);
    }

    @Override
    public void showTokenDetailsUi(String TokenId) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
//        Intent intent = new Intent(getContext(), TokenDetailActivity.class);
//        intent.putExtra(TokenDetailActivity.EXTRA_Token_ID, TokenId);
//        startActivity(intent);
    }

    @Override
    public void showTokenMarkedComplete() {
        showMessage(getString(R.string.token_marked_complete));
    }

    @Override
    public void showTokenMarkedActive() {
        showMessage(getString(R.string.token_marked_active));
    }

    @Override
    public void showTokenMarkedCancel() {
        showMessage(getString(R.string.token_marked_cancel));
    }

    @Override
    public void showCompletedTokensCleared() {
        showMessage(getString(R.string.completed_tokens_cleared));
    }

    @Override
    public void showLoadingTokensError() {
        showMessage(getString(R.string.loading_tokens_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    public interface TokenItemListener {

        void onTokenClick(Token clickedToken);

        void onCompleteTokenClick(Token completedToken);

        void onActivateTokenClick(Token activatedToken);

        void onCancelTokenClick(Token activatedToken);
    }
}