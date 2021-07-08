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

    public List<String> getAllIndexWords(TMSAnalyzer analyzer) {
        return Stream.of(subWord.split(","), new String[]{mainWord})
                .flatMap(Arrays::stream)
                .map(w -> analyzer.getIndexWords(w).toLowerCase())
                .filter(w -> StringUtils.isNotEmpty(w))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public String toSynonymQuery(TMSAnalyzer analyzer) {
        return getAllIndexWords(analyzer).stream()
                .map(w -> String.format("(%s)", w))
                .collect(Collectors.joining(" OR "))
                .trim();
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
