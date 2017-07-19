package io.github.funkynoodles.classlookup.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import io.github.funkynoodles.classlookup.R;

public class LoadingScheduleDialogFragment extends DialogFragment {

    public static LoadingScheduleDialogFragment newInstance() {
        LoadingScheduleDialogFragment loadingScheduleDialogFragment = new LoadingScheduleDialogFragment();

        return loadingScheduleDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.loading_schedules_dialog_fragment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.loading_schedules_dialog_fragment)
                .setTitle(R.string.textLoadingSchedule);
        return builder.create();
    }
}
