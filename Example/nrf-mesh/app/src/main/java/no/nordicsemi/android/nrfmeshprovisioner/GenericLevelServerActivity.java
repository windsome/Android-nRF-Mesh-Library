package no.nordicsemi.android.nrfmeshprovisioner;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import javax.inject.Inject;

import no.nordicsemi.android.meshprovisioner.configuration.MeshModel;
import no.nordicsemi.android.meshprovisioner.configuration.ProvisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.models.GenericLevelServerModel;

public class GenericLevelServerActivity extends BaseModelConfigurationActivity {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    private SeekBar mLevelSeekBar;

    @Override
    protected final void addControlsUi(final MeshModel model) {
        if (model instanceof GenericLevelServerModel) {
            final CardView cardView = findViewById(R.id.node_controls_card);
            final View nodeControlsContainer = LayoutInflater.from(this).inflate(R.layout.layout_generic_level, cardView);

            final TextView level = nodeControlsContainer.findViewById(R.id.level);
            mLevelSeekBar = nodeControlsContainer.findViewById(R.id.level_seekbar);
            mLevelSeekBar.setProgress(0);
            mLevelSeekBar.setMax(100);

            mActionRead = nodeControlsContainer.findViewById(R.id.action_read);
            mActionRead.setOnClickListener(v -> {
                final ProvisionedMeshNode node = (ProvisionedMeshNode) mViewModel.getExtendedMeshNode().getMeshNode();
                mViewModel.sendGenericLevelGet(node);
                showProgressbar();
            });

            mViewModel.getGenericLevelState().observe(this, genericLevelStatusUpdate -> {
                hideProgressBar();
                final int presentLevel = genericLevelStatusUpdate.getPresentLevel();
                final Integer targetLevel = genericLevelStatusUpdate.getTargetLevel();
                final int levelPercent;
                if (targetLevel == null) {
                    levelPercent = ((presentLevel + 32768) * 100) / 65535;
                    level.setText(getString(R.string.generic_level_percent, levelPercent));
                } else {
                    levelPercent = ((targetLevel + 32768) * 100) / 65535;
                    level.setText(getString(R.string.generic_level_percent, levelPercent));
                }
                mLevelSeekBar.setProgress(levelPercent);
            });

            mLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                    level.setText(getString(R.string.generic_level_percent, progress));
                    if(fromUser)
                        sendGenericLevel(progress);
                }

                @Override
                public void onStartTrackingTouch(final SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                }
            });
        }
    }

    private void sendGenericLevel(final int level) {
        if (level % 10 == 0) {
            final ProvisionedMeshNode node = (ProvisionedMeshNode) mViewModel.getExtendedMeshNode().getMeshNode();
            final int genericLevel = ((level * 65535) / 100) - 32768;
            mViewModel.sendGenericLevelSetUnacknowledged(node, genericLevel, null, null, null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void enableClickableViews() {
        super.enableClickableViews();
    }

    @Override
    protected void disableClickableViews() {
        super.disableClickableViews();
    }
}
