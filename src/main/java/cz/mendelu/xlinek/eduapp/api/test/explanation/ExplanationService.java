package cz.mendelu.xlinek.eduapp.api.test.explanation;

import cz.mendelu.xlinek.eduapp.api.test.content.TestContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExplanationService {

    @Autowired
    TestContentService testContentService;
    @Autowired
    ExplanationRepository explanationRepository;

    /* ---- INSERT ---- */

    /**
     * Funkce slouzi k vytvoreni noveho zaznamu o vysvetleni urcite otazky.
     * @param text = textova vysvetlivka
     * @param picture = obrazek
     * @param equation = rovnice
     * @return vraci nove vytvoreny objekt jinak null
     */
    public Explanation newExplanation(String text, String picture, String equation){

        if (text.equals(""))
            return null;

        Explanation explanation = new Explanation();

        explanation.setTestContent(testContentService.newTestContent(text, picture, equation));

        return explanationRepository.save(explanation);
    }
}
