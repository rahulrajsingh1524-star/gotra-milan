package com.raj.gotramilan;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {

    static class Gotra {
        String name, vansh;
        Gotra(String name, String vansh) { this.name = name; this.vansh = vansh; }
        @Override public String toString() { return name + "  (" + vansh + ")"; }
    }

    // Initial master list transcribed from the user-provided chart.
    // Variants/spelling can be edited later after user verification.
    final ArrayList<Gotra> gotras = new ArrayList<>();

    void add(String vansh, String csv) {
        for (String s : csv.split(",")) {
            String n = s.trim();
            if (!n.isEmpty()) gotras.add(new Gotra(n, vansh));
        }
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        add("अनहलवंशीय (चौहान)",
            "देवावत,सूरावत,उदावत,लगेत,राजोरिया,बिगड़ियात,धाडियात,नाडोत,विरमोत,काठात,कणियात,मुंडियात,भैंरोत,लाडात,बोखाडा,नणेत,औंधात,अमरास्या,जेहलात,भादावत,भूंडावत,पापडियात,विलोत,बराणा,भोपावत,बादडियात,पालोत,बलाड़िया,चाचकियात,झामर,नापात,नेतावत,पाखरियात,मलेत,चीता,तेजावत,घोड़ात,कुंभात,बिजलोत,नोमडियात,दामावत,ननोमा");
        add("अनूपवंशीय चौहान (बरड़)",
            "खोंखावत,मेघावत,बाहडोत,पदमावत,हीराबत,मालावत,आपावत,कानावत,पातलावत,डूंगावत,सुजावत,भीलावत,वरात,झूंठा,धकोत,धामावत,लखियात,सहड़ोत,माणकात,करणात,भोपात,चांदावत,सेंगणोत,टाटोक,नंगावत,कूंपावत,रत्नावत,कीतावत,खीमावत,सोड़ावत,मोकावत,लाखावत,राजावत,हालावत,जैतरात,सीयावत,तेजावत,तावेड़,रूपावत");
        add("गहलोतवंशीय",
            "कील (भीलाव),गरतूंड,डांगा,बाणियात,लोहरा,वालोत,विटोल,महेंद्रात,गोदात,भूंडक,डाकल,वहलोल,काठ,काछी,दायमा,मननात,बावलात,धर्मलात,बरगट,मोटा,मादात,बूज,भाभला,भूचर,धोरणा,कीट,चरेड़,नाडियावत,कनडावत,भलियावत");
        add("पंवारवंशीय",
            "मोटिस,धोधिंग,बोया,देहलात,कलात,खिंयात,जेहलात,मूंजी,बाणियात,कठारा");
        add("राठौड़वंशीय",
            "पोकरिया,भरड़,मामणियात,बूज,बरलात,राजवंशीय,बोच");
        add("भाटीवंशीय",
            "सहलोत,चौरोट,ठेकरोत,पापलियात,जालात,वाणात,रहलोत");
        add("सोलह साखी",
            "संडासी,कड़ेवाल,चांदा,माल,हरमर,बागड़ी,फबड़ी,डाडरवाल,जाटरवाल,ओसवाल,नखवाल,हुंडासी,खरमर,चरडू,हला,खींची");

        final ArrayList<String> labels = new ArrayList<>();
        labels.add("— गोत्र चुनें —");
        for (Gotra g : gotras) labels.add(g.toString());

        int[] ids = {R.id.boyGotra,R.id.boyMotherGotra,R.id.boyGrandmotherGotra,
                     R.id.girlGotra,R.id.girlMotherGotra,R.id.girlGrandmotherGotra};
        for (int id : ids) {
            Spinner sp = findViewById(id);
            sp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels));
        }

        findViewById(R.id.checkButton).setOnClickListener(v -> {
            Spinner bg = findViewById(R.id.boyGotra);
            Spinner bm = findViewById(R.id.boyMotherGotra);
            Spinner bd = findViewById(R.id.boyGrandmotherGotra);
            Spinner gg = findViewById(R.id.girlGotra);
            Spinner gm = findViewById(R.id.girlMotherGotra);
            Spinner gd = findViewById(R.id.girlGrandmotherGotra);

            Gotra boy = get(bg), boyM = get(bm), boyD = get(bd);
            Gotra girl = get(gg), girlM = get(gm), girlD = get(gd);

            TextView out = findViewById(R.id.result);

            if (boy == null || boyM == null || boyD == null || girl == null || girlM == null || girlD == null) {
                out.setText("⚠️ सभी 6 गोत्र चुनें।");
                out.setTextColor(Color.DKGRAY);
                return;
            }

            ArrayList<String> problems = new ArrayList<>();

            // Rule 1: no exact gotra may be repeated in the six entered gotras.
            Gotra[] six = {boy,boyM,boyD,girl,girlM,girlD};
            for (int i=0;i<six.length;i++) {
                for (int j=i+1;j<six.length;j++) {
                    if (six[i].name.equalsIgnoreCase(six[j].name)) {
                        problems.add("समान गोत्र मिला: " + six[i].name);
                    }
                }
            }

            // Rule 2: as stated in the chart, the girl's three gotras
            // should not be from the boy's own vansh.
            String boyVansh = boy.vansh;
            for (Gotra g : new Gotra[]{girl,girlM,girlD}) {
                if (g.vansh.equals(boyVansh)) {
                    problems.add("लड़की की गोत्र '" + g.name + "' लड़के के वंश (" + boyVansh + ") में है।");
                }
            }

            // Same-vansh relationship on the girl's side is also shown for transparency.
            if (girlM.vansh.equals(girl.vansh) || girlD.vansh.equals(girl.vansh)) {
                problems.add("लड़की की ओर माँ/दादी की गोत्र उसके अपने वंश में भी आ रही है; इसे अलग से परिवार की मान्यता के अनुसार जाँचें।");
            }

            if (problems.isEmpty()) {
                out.setText("✅ दिए गए चार्ट के नियमों के अनुसार कोई टकराव नहीं मिला।");
                out.setTextColor(Color.rgb(0,120,60));
            } else {
                StringBuilder s = new StringBuilder("❌ जाँच में ये बातें मिलीं:\n\n");
                for (String p : problems) s.append("• ").append(p).append("\n");
                out.setText(s.toString());
                out.setTextColor(Color.rgb(170,0,0));
            }
        });
    }

    Gotra get(Spinner sp) {
        int pos = sp.getSelectedItemPosition();
        if (pos <= 0) return null;
        return gotras.get(pos-1);
    }
}
