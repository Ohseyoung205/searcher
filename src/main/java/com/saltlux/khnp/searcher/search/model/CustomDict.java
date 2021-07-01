package com.saltlux.khnp.searcher.search.model;

import com.saltlux.khnp.searcher.deepqa.analyzer.TMSAnalyzer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "custom_dictionary")
public class CustomDict {

    public enum WORD_DIV {
        B, // 불용어
        D  // 동의어
    }

    @Id
    private Long wordId;

    @Column
    private String mainWord;

    @Column
    private String subWord;

    @Column
    @Enumerated(EnumType.STRING)
    private WORD_DIV wordDiv;

    @Column
    private String useYn;

    @Column
    @Type(type = "yes_no")
    private boolean recYn;

    @Column
    private Date createDt;
    // FIXME 사전에서 가져와서 색인할때는 indexword를 추출할 필요가 있지만
    // 런타임 쿼리에서 indexword를 추출할때에는 단어별로 형분석기를 다시 돌릴 필요가 없긴하다.
    public List<String> getIndexWords(TMSAnalyzer analyzer) {
        return Stream.of(subWord.split(","), new String[]{mainWord})
                .flatMap(Arrays::stream)
                .map(w -> analyzer.getIndexWords(w).toLowerCase())
                .filter(w -> StringUtils.isNotEmpty(w))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public String toSynonymQuery(TMSAnalyzer analyzer) {
        return getIndexWords(analyzer).stream()
                .map(w -> String.format("(%s)", w))
                .collect(Collectors.joining(" OR "));
    }

    @Override
    public int hashCode() {
        return (int) (wordId ^ wordId >>> 32);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomDict other = (CustomDict) o;
        return other.hashCode() == this.hashCode();
    }

}
