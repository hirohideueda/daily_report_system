package actions.views;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

//日報情報について画面の入力値・出力値を扱うViewモデル
public class ReportView {

    //id
    private EmployeeView employee;

    //日報日付
    private LocalDate reportDate;

    //日報タイトル
    private String title;

    //日報内容
    private String content;

    //登録日時
    private LocalDateTime createdAt;

    //更新日時
    private LocalDateTime updatedAt;
}
