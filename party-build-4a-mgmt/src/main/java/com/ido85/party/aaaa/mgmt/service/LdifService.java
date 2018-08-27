package com.ido85.party.aaaa.mgmt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapAttributes;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.ldif.batch.LdifAggregator;
import org.springframework.ldap.ldif.parser.LdifParser;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

/**
 * ldif 工具类，导入导出
 * Created by rongxj on 2017/4/10.
 */
@Slf4j
@Named
public class LdifService {

    @Inject
    protected LdapTemplate ldapTemplate;

    @Value("${ldap.base-dn}")
    protected String baseDn;


    /**
     * 导入ldif文件到ldap服务器
     * @param file ldif文件
     */
    public void importLdif(String file){
        importLdif(new File(file));
    }

    /**
     * 导入ldif文件到ldap服务器
     * @param file ldif文件
     */
    public void importLdif(File file) {
        LdifParser parser = new LdifParser(file);
        try {
            parser.open();
            while(parser.hasMoreRecords()){
                LdapAttributes att = parser.getRecord();
                System.out.println(att);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }finally{
            try {
                parser.close();
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }
    }

    /**
     * 根据用户组导出用户到ldif文件
     * @param ou 用户组<br/>
     *           * 全部用户， JINAN 济南用户
     * @param exportFile<br/>
     *          导出到系统文件
     */
    public void exportLdif(String ou, String exportFile){
        exportLdif(ou, new File(exportFile));
    }

    /**
     * 根据用户组导出用户到ldif文件
     * @param ou 用户组<br/>
     *           * 全部用户， JINAN 济南用户
     * @param exportFile<br/>
     *          导出到系统文件
     */
    @SuppressWarnings({"rawtypes", "unchecked" })
    public void exportLdif(@NotNull final String ou, File exportFile) {
//        final List<LdapAttributes> res = new ArrayList<LdapAttributes>();
//        //首先遍历ou
//        if("*".equals(ou)){
//            ldapTemplate.search(String.format("ou=%s", ou), "(objectclass=*)",  new CollectingNameClassPairCallbackHandler(){
//
//                @Override
//                public Object getObjectFromNameClassPair(NameClassPair nameClassPair) throws NamingException {
//                    nameClassPair.getName();
//                    SearchResult searchResult = (SearchResult) nameClassPair;
//                    LdapAttributes attributes = new LdapAttributes();
//                    attributes.setName(LdapUtils.newLdapName(nameClassPair.getName()));
//                    Enumeration enumer = searchResult.getAttributes().getAll();
//                    while (enumer.hasMoreElements()) {
//                        Attribute at = (Attribute) enumer.nextElement();
//                        attributes.put(at);
//                    }
//                    res.add(attributes);
//                    return attributes;
//                }
//            });
//        }
        Assert.hasLength(ou, "用户组不能为空");
        Assert.doesNotContain(ou, "*", String.format("非法的用户组: %s",ou));


        List<Attributes> list = ldapTemplate.search(String.format("ou=%s", ou), "(objectclass=*)", new AttributesMapper() {

            @Override
            public Object mapFromAttributes(Attributes attributes)
                    throws NamingException {
                LdapAttributes attr = new LdapAttributes();
                String name;
                if (attributes.get("uid") != null) {

                    name = String.format("uid=%s,ou=%s,%s", attributes.get("uid").get(), ou, baseDn);//attributes.get("ou").get(), baseDn);
                } else {
                    name = String.format("ou=%s,%s", ou, baseDn);
                }
                attr.setName(LdapUtils.newLdapName(name));
                Enumeration enumer = attributes.getAll();
                while (enumer.hasMoreElements()) {
                    Attribute at = (Attribute) enumer.nextElement();
                    attr.put(at);
                }
                return attr;
            }

        });
        FlatFileItemWriter writer = getWriter(exportFile);
        try {
            writer.open(new ExecutionContext());
            writer.write(list);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            writer.close();
        }
    }

    protected static FlatFileItemWriter<Attributes> getWriter(File exportFile){
        FlatFileItemWriter writer = new FlatFileItemWriter();
        writer.setResource(new FileSystemResource(exportFile));
        writer.setLineAggregator(new LdifAggregator());
        return writer;
    }
}
