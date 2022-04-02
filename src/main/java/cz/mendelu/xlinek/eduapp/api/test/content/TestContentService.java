package cz.mendelu.xlinek.eduapp.api.test.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestContentService {
    @Autowired
    TestContentRepository testContentRepository;

    /* ---- INSERT ---- */

    /**
     * Funkce slouzi k vytvoreni noveho zaznamu typu TestContent
     * @param text = text
     * @param picture = zdroj k obrázku
     * @param equation = rovnice
     * @return vraci nove vytvoreny objekt
     */
    public TestContent newTestContent(String text, String picture, String equation){
        TestContent testContent = new TestContent();

        if (text.equals(""))
            return null;

        testContent.setText(text);
        testContent.setPicture(picture);
        testContent.setEquation(equation);

        return testContentRepository.save(testContent);
    }
}
