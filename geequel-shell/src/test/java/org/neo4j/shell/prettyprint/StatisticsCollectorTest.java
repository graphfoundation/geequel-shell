package org.neo4j.shell.prettyprint;

import org.junit.Test;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.summary.SummaryCounters;
import org.neo4j.shell.cli.Format;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatisticsCollectorTest {

    @Test
    public void returnEmptyStringForPlainFormatting() throws Exception {
        // given
        ResultSummary result = mock(ResultSummary.class);

        // when
        String actual = new StatisticsCollector(Format.PLAIN).collect(result);

        // then
        assertThat(actual, is(""));
    }

    @Test
    public void returnStatisticsForDefaultFormatting() throws Exception {
        // given
        ResultSummary resultSummary = mock(ResultSummary.class);
        SummaryCounters summaryCounters = mock(SummaryCounters.class);

        when(resultSummary.counters()).thenReturn(summaryCounters);
        when(summaryCounters.labelsAdded()).thenReturn(1);
        when(summaryCounters.nodesCreated()).thenReturn(10);

        // when
        String actual = new StatisticsCollector(Format.VERBOSE).collect(resultSummary);

        // then
        assertThat(actual, is("Added 10 nodes, Added 1 labels"));
    }

}
