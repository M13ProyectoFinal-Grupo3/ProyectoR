package com.example.proyector;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TicketActivity extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference myRef1 = db.collection("ticket");

    private Spinner lista;
    ArrayAdapter<String> adaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tickets, container, false);

/*  TODO - ESTO EN CASO DE QUERER CONSULTAR UNA LISTA DE TICKETS
        // Obtener los datos de Firebase
        myRef1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> ticketList = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String ticket = document.getString("nombreDelCampo"); // TODO Reemplazar "nombreDelCampo" con el nombre real del campo en Firebase
                    ticketList.add(ticket);
                }

                // Rellenar el Spinner con los datos obtenidos
                adaptador = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, ticketList);
                lista.setAdapter(adaptador);
            }
        });


        //inicializamos las variables
        lista = view.findViewById(R.id.elegirTicket);
 */


        return view;
    }



    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
/*
    private ViewPager2 viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */

/*    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
*/
    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
 /*
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return new ScreenSlidePageFragment();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }*/
}