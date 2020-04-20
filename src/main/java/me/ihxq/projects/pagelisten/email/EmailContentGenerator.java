package me.ihxq.projects.pagelisten.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author xq.h
 * 2020/4/19 23:18
 **/
@Slf4j
@Service
@SuppressWarnings("HtmlDeprecatedAttribute")
public class EmailContentGenerator {
    //language=HTML
    private static final String REPORT_TEMPLATE = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\n" +
            "<html xmlns='http://www.w3.org/1999/xhtml' lang='zh-cn'>\n" +
            "    <head>\n" +
            "        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n" +
            "        <title></title>\n" +
            //"        <style></style>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <table border='0' cellpadding='0' cellspacing='0' width='100%' id='bodyTable'>\n" +
            "            <tr>\n" +
            "                <td align='center' valign='top'>\n" +
            "                    <table border='0' cellpadding='20' cellspacing='0' width='600' id='emailContainer'>\n" +
            "                        <tr>\n" +
            "                            <td align='center' valign='top'>\n" +
            "                                <table border='0' cellpadding='20' cellspacing='0' width='100%' id='summary'>\n" +
            "                                    <tr>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            ${summary}\n" +
            "                                        </td>\n" +
            "                                    </tr>\n" +
            "                                </table>\n" +
            "                            </td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td align='center' valign='top'>\n" +
            "                                <table border='1' cellpadding='20' cellspacing='0' width='100%' id='detail'>\n" +
            "                                    <tr>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            Name\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            Description\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            Hit\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            Current\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            Operator\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            Target\n" +
            "                                        </td>\n" +
            "                                    </tr>\n" +
            "                                    ${detailRows}" +
            "                                </table>\n" +
            "                            </td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "    </body>\n" +
            "</html>";

    //language=HTML
    private static final String DETAIL_ROW_TEMPLATE = "<tr bgcolor='${bgColor}'>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            ${name}\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            ${description}\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            ${hit}\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            ${currentValue}\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            ${operator}\n" +
            "                                        </td>\n" +
            "                                        <td align='center' valign='top'>\n" +
            "                                            ${targetValue}\n" +
            "                                        </td>\n" +
            "                                    </tr>\n";

    private String serializeToHtml(ChangeRecord record) {
        return DETAIL_ROW_TEMPLATE.replace("${name}", record.getName())
                .replace("${description}", record.getDescription())
                .replace("${hit}", record.getHit() == null ? "-" : record.getHit() ? "YES" : "NO")
                .replace("${currentValue}", Optional.ofNullable(record.getCurrentValue()).orElse(""))
                .replace("${operator}", record.getOperator())
                .replace("${targetValue}", record.getTargetValue())
                .replace("${resultDescription}", Optional.ofNullable(record.getResultDescription()).orElse(""))
                .replace("${bgColor}", record.isSuccess() ? Boolean.TRUE.equals(record.getHit()) ? "lightblue" : "" : "lightgray")
                .replace("¥", "&yen;");
    }

    public String serializeToHtml(ChangeReport report) {
        String detalRows = report.getDetail().stream()
                .map(this::serializeToHtml)
                .collect(Collectors.joining());
        String summary = "Hit: " + report.getHit()
                + "; Total: " + report.getTotal()
                + "; Success: " + report.getSuccess()
                + "; Failure: " + report.getFailure()
                + "; Run duration: " + Duration.between(report.getStartTime(), report.getEndTime())
                + ";";
        return REPORT_TEMPLATE.replace("${summary}", summary)
                .replace("${detailRows}", detalRows);
    }
}
