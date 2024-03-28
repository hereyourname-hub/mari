package com.example.bodyboost.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import com.example.bodyboost.AddMedication;
import com.example.bodyboost.Helpers.DBHelper;
import com.example.bodyboost.Helpers.TimeFormatting;
import com.example.bodyboost.MedicationNotes;
import com.example.bodyboost.R;
import com.example.bodyboost.SimpleClasses.Medication;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyMedicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyMedicationsFragment extends Fragment {
    TextView name;
    TextView dosage;
    TextView alias;
    TextView frequency;
    TextView takenSince;
    Button notesButton;
    Button editButton;

    public MyMedicationsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyMedicationsFragment.
     */
    public static MyMedicationsFragment newInstance() {
        return new MyMedicationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Medication med = requireArguments().getParcelable("Medication");

        final View rootView = inflater.inflate(R.layout.fragment_my_medications, container, false);

        insertMedicationData(med, rootView);

        return rootView;
    }

    private void insertMedicationData(Medication medication, View v) {
        DBHelper db = new DBHelper(getContext());
        LocalTime[] times = db.getMedicationTimes(medication.getId());
        LocalDateTime[] dateTimes = new LocalDateTime[times.length];
        String dosageVal;

        name = v.findViewById(R.id.myMedCardMedicationName);
        dosage = v.findViewById(R.id.myMedCardDosage);
        alias = v.findViewById(R.id.myMedCardAlias);
        frequency = v.findViewById(R.id.myMedCardFrequency);
        takenSince = v.findViewById(R.id.myMedCardTakenSince);
        notesButton = v.findViewById(R.id.myMedsNotes);
        editButton = v.findViewById(R.id.myMedsEdit);

        for (int i = 0; i < times.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateTimes[i] = LocalDateTime.of(medication.getStartDate().toLocalDate(), times[i]);
            }
        }

        medication.setTimes(dateTimes);

        if (medication.getDosage() == (int) medication.getDosage()) {
            dosageVal = String.format(Locale.getDefault(), "%d", (int) medication.getDosage());
        } else {
            dosageVal = String.valueOf(medication.getDosage());
        }

        name.setText(getString(R.string.med_name, medication.getName()));
        dosage.setText(getString(R.string.dosage, dosageVal, medication.getDosageUnits()));

        frequency.setText(medication.generateFrequencyLabel(getContext()));

        if (!medication.getAlias().equals("")) {
            alias.setVisibility(View.VISIBLE);
            alias.setText(getString(R.string.alias_lbl, medication.getAlias()));
        }

        String beginning = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            beginning = TimeFormatting.localDateToString(
                    medication.getParent() == null ? medication.getStartDate().toLocalDate() : medication.getParent().getStartDate().toLocalDate()
            );
        }

        takenSince.setText(getString(R.string.taken_since, beginning));

        Intent notesIntent = new Intent(getActivity(), MedicationNotes.class);
        notesIntent.putExtra("medId", medication.getId());

        notesButton.setOnClickListener(view ->
        {
            getActivity().finish();
            getActivity().startActivity(notesIntent);
        });

        Intent editMedIntent = new Intent(getActivity(), AddMedication.class);
        editMedIntent.putExtra("medId", medication.getId());

        editButton.setOnClickListener(view ->
        {
            getActivity().finish();
            getActivity().startActivity(editMedIntent);
        });
    }
}
