import com.shfb.oframe.core.util.common.Encrypt
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.properties.PropertiesUtil
import com.shfb.oframe.core.web.controller.GroovyController
import groovy.sql.Sql
import org.apache.commons.io.FileUtils

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 房产附件整理
 * <p>
 * @author linql 创建 2012-3-18
 */

public class arrangeFile extends GroovyController {

    int k = 1;
    String upFileFolder = PropertiesUtil.readPath("oframeweb", "com.shfb.oframe.core.web.upfile.folder");

    /**
     * 整理房产附件,并整理该房产下所有产权人，户籍人员 附件
     * @param request 请求信息
     * @param response
     */
    public void arrange(HttpServletRequest request, HttpServletResponse response) {
        //查数据库获得地址
        Class.forName("com.mysql.jdbc.Driver");
        String url = PropertiesUtil.readString("jdbc", "jdbc.core.url");
        String username = PropertiesUtil.readString("jdbc", "jdbc.core.username");
        String password = PropertiesUtil.readString("jdbc", "jdbc.core.password");
        Connection con = DriverManager.getConnection(url, Encrypt.decryptByDES(username), Encrypt.decryptByDES(password));
        con.setAutoCommit(true);
        Sql sql = new Sql(con);


        String hsAddr = "";
        sql.query("""SELECT t1.doc_id, t2.hs_addr, t2.hs_cd, t1.doc_path, t2.old_hs_id FROM svc_doc t1, prj_old_hs t2 WHERE t1.rel_type = 3 and t1.rel_id = t2.old_hs_id""") {
            ResultSet rs ->
                while (rs.next()) {
                    hsAddr = rs.getString("hs_addr");
                    String hsCd = rs.getString("hs_cd");
                    Integer docId = rs.getInt("doc_id");
                    //取文件名，不包含路径
                    String docPath = rs.getString("doc_path");
                    String oldHsId = rs.getString("old_hs_id");
                    //整理路径，一条数据匹配一次路径
                    String path = arrangePath(hsAddr, hsCd);

                    //处理自身房产附件
                    mFile(sql, docPath, path, docId);
                    //处理产权人协议附件
                    arrangeOwnerFile(sql, oldHsId, path);
                    //处理户籍人员附件
                    arrangePersonFile(sql, oldHsId, path);
                }
        };
    }

    /**
     * 整理户籍人员附件
     */
    public void arrangePersonFile(Sql sql, String oldHsId, String path) {
        sql.query("""SELECT
                              t1.doc_id,
                              t1.doc_path
                              FROM
                              svc_doc t1,
                              person_info t2
                              WHERE t1.rel_type = 2
                              and t1.rel_id = t2.person_id
                              and t2.old_hs_id = ? """, [oldHsId]) {
            ResultSet resultSet ->
                while (resultSet.next()) {
                    String docPath = resultSet.getString("doc_path");
                    Integer docId = resultSet.getInt("doc_id");
                    mFile(sql, docPath, path, docId);
                }
        }
    }

    /**
     * 整理产权人协议附件信息
     */
    public void arrangeOwnerFile(Sql sql, String oldHsId, String path) {
        sql.query("""SELECT
                            t1.doc_id,
                            t1.doc_path
                            FROM
                            svc_doc t1,prj_old_hs_owner t2
                            WHERE t1.rel_type = 4
                            AND t1.rel_id IN (
                            CONCAT(t2.hs_owner_id, '_ct_1'),
                            CONCAT(t2.hs_owner_id, '_ct_4'),
                            CONCAT(t2.hs_owner_id, '_ct_cfm_1'),
                            CONCAT(t2.hs_owner_id, '_ct_cfm_scan_1'))
                            AND t2.old_hs_id = ?""", [oldHsId]) {
            ResultSet resultSet ->
                while (resultSet.next()) {
                    String docPath = resultSet.getString("doc_path");
                    Integer docId = resultSet.getInt("doc_id");
                    mFile(sql, docPath, path, docId);
                }
        }
    }

    /**
     * 根据地址整理 创建文件夹，返回路径
     */
    public String arrangePath(String hsAddr, String hsCd) {
        //调用正则匹配地址来建立文件夹
        String addrMatchPatter = "(.*)社区(.*)胡同(.*?)号楼(.*)";
        Pattern pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(hsAddr);
        String sq, ht, yl, mh, another;
        boolean match = false;
        if (matcher.find() && matcher.groupCount() == 4) {
            sq = matcher.group(1);
            ht = matcher.group(2);
            yl = matcher.group(3);
            mh = matcher.group(4);
            match = true;
        }
        if (!match) {
            addrMatchPatter = "(.*)社区(.*)胡同(.*?)号院(.*)";
            pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(hsAddr);
            if (matcher.find() && matcher.groupCount() == 4) {
                sq = matcher.group(1);
                ht = matcher.group(2);
                yl = matcher.group(3);
                mh = matcher.group(4);
                match = true;
            }
        }
        if (!match) {
            addrMatchPatter = "(.*)社区(.*)胡同(.*?)号(.*)";
            pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(hsAddr);
            if (matcher.find() && matcher.groupCount() == 4) {
                sq = matcher.group(1);
                ht = matcher.group(2);
                yl = matcher.group(3);
                mh = matcher.group(4);
                match = true;
            }
        }
        if (!match) {
            addrMatchPatter = "(.*)社区(.*)大街(.*?)号院(.*)";
            pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(hsAddr);
            if (matcher.find() && matcher.groupCount() == 4) {
                sq = matcher.group(1);
                ht = matcher.group(2);
                yl = matcher.group(3);
                mh = matcher.group(4);
                match = true;
            }
        }
        if (!match) {
            addrMatchPatter = "(.*)社区(.*)大街(.*?)号楼(.*)";
            pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(hsAddr);
            if (matcher.find() && matcher.groupCount() == 4) {
                sq = matcher.group(1);
                ht = matcher.group(2);
                yl = matcher.group(3);
                mh = matcher.group(4);
                match = true;
            }
        }
        if (!match) {
            addrMatchPatter = "(.*)社区(.*)大街(.*?)号(.*)";
            pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(hsAddr);
            if (matcher.find() && matcher.groupCount() == 4) {
                sq = matcher.group(1);
                ht = matcher.group(2);
                yl = matcher.group(3);
                mh = matcher.group(4);
                match = true;
            }
        }
        if (!match) {
            addrMatchPatter = "(.*)社区(.*)巷(.*?)号院(.*)";
            pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(hsAddr);
            if (matcher.find() && matcher.groupCount() == 4) {
                sq = matcher.group(1);
                ht = matcher.group(2);
                yl = matcher.group(3);
                mh = matcher.group(4);
                match = true;
            }
        }
        if (!match) {
            addrMatchPatter = "(.*)社区(.*)巷(.*?)号(.*)";
            pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(hsAddr);
            if (matcher.find() && matcher.groupCount() == 4) {
                sq = matcher.group(1);
                ht = matcher.group(2);
                yl = matcher.group(3);
                mh = matcher.group(4);
                match = true;
            }
        }
        if (!match) {
            addrMatchPatter = "(.*)";
            pattern = Pattern.compile(addrMatchPatter, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(hsAddr);
            if (matcher.find() && matcher.groupCount() == 1) {
                another = matcher.group(1);
                match = true;
            }
        }

        String pathStr = "\\附件资料";
        if (StringUtil.isNotEmptyOrNull(sq)) {
            pathStr = pathStr + "\\${sq}社区";
        }
        if (StringUtil.isNotEmptyOrNull(ht)) {
            pathStr = pathStr + "\\${ht}胡同";
        }
        if (StringUtil.isNotEmptyOrNull(yl)) {
            pathStr = pathStr + "\\${yl}院落";
        }
        if (StringUtil.isNotEmptyOrNull(mh)) {
            pathStr = pathStr + "\\${mh}";
        }


        String path;
        if (StringUtil.isEmptyOrNull(another)) {
            path = StringUtil.formatFilePath(upFileFolder + pathStr + "\\${hsCd}");
        } else {
            path = StringUtil.formatFilePath(upFileFolder + "\\地址不清晰\\${hsCd}");
        }

        return path;
    }

    //处理文件
    public void mFile(Sql sql, String docPath, String path, Integer docId) {
        //根据路径创建文件夹
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        /*移动文件*/
        File srcFile = new File(upFileFolder + File.separator + "${docPath}");

        String fileName = srcFile.getName();
        String updatePath = path.replaceAll(upFileFolder,"") + File.separator + fileName;
        log.debug("【path路径】" + path);
        log.debug("【docPath路径】" + docPath);
        log.debug("【updatePath路径】" + updatePath);

        if (srcFile.exists() && !updatePath.equals(docPath)) {
            FileUtils.moveFileToDirectory(srcFile, file, false);
//            将更新的路径写入数据库
            int resultInt = sql.executeUpdate("update svc_doc SET doc_path=${updatePath} where doc_id = $docId");
            log.debug("【文件拷贝，处理结果：${resultInt}】");
        } else {
            log.debug("【文件不存在，或已移走。】");
        }
        log.debug("处理了 ${k++} 条数据");
    }
}