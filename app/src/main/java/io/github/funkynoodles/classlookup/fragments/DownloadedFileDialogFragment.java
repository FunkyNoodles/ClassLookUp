package io.github.funkynoodles.classlookup.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.models.MetaTerm;
import io.github.funkynoodles.classlookup.tasks.DownloadTermTask;

public class DownloadedFileDialogFragment extends DialogFragment {

    public static DownloadedFileDialogFragment newInstance(MetaTerm metaTerm) {
        DownloadedFileDialogFragment downloadedFileDialogFragment = new DownloadedFileDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("metaTerm", metaTerm);
        downloadedFileDialogFragment.setArguments(args);

        return downloadedFileDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getArguments() != null) {
            final MetaTerm metaTerm = (MetaTerm) getArguments().getSerializable("metaTerm");
            assert metaTerm != null;
            builder.setTitle(metaTerm.getText())
                    .setItems(R.array.downloadedFileOption, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    // Re-download
                                    metaTerm.setDownloadTermTask(new DownloadTermTask(metaTerm, getActivity()));
                                    metaTerm.executeDownloadTermTask();
                                    break;
                                case 1:
                                    // Delete file
                                    File dir = getActivity().getDir("schedules", Context.MODE_PRIVATE);
                                    if (!dir.exists()) {
                                        deleteFileFailed(metaTerm);
                                    }
                                    File file = new File(dir, metaTerm.getText() + ".json");
                                    boolean deleted = file.delete();
                                    if (deleted) {
                                        metaTerm.getDownloadedButton().setVisibility(View.GONE);
                                        metaTerm.getDownloadButton().setVisibility(View.VISIBLE);
                                    } else {
                                        deleteFileFailed(metaTerm);
                                    }
                            }
                        }
                    });
        }
        return builder.create();
    }

    public void deleteFileFailed(final MetaTerm metaTerm) {
        Toast.makeText(getActivity(), getString(R.string.textCannotDeleteFile), Toast.LENGTH_LONG).show();
        metaTerm.getDownloadedButton().setVisibility(View.GONE);
        metaTerm.getDownloadButton().setVisibility(View.VISIBLE);
    }
}
