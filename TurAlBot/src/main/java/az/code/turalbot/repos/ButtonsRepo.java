package az.code.turalbot.repos;

import az.code.turalbot.models.Button;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ButtonsRepo extends JpaRepository<Button, Long> {

    @Query("select b from Button b where b.language.id=:lId and b.question.id=:qId")
    List<Button> getButtons(long qId, Long lId);

    @Query("select b from Button b where b.buttonCallBack=:callBack and b.language.id=:lId")
    Button getButtonWithCallBackAndLangId(String callBack, Long lId);

}
