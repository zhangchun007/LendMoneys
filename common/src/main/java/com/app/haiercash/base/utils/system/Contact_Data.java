package com.app.haiercash.base.utils.system;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.Data;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by use on 2016/4/26.
 * 16/4/26
 * 贺庆信
 * 获取手机通讯录信息
 */
public class Contact_Data {

    /**
     * 联系人显示名称
     */
    public static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 联系人名称
     **/
    public static ArrayList<String> mContactsName = new ArrayList<>();

    /**
     * 电话号码
     **/
    public static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 联系人的ID
     **/
    public static final int PHONES_CONTACT_ID_INDEX = 3;

    /**
     * 联系人号码
     **/
    public static ArrayList<String> mContactsNumber = new ArrayList<>();

    public static ArrayList<Object> CONTACTS_LIST = new ArrayList<>();

    public static ArrayList<Object> CONTACTS_LISTS = new ArrayList<>(); //联系人所有信息


    /**
     * 获取库Phon表字段
     */
    public static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    };

    /**
     * 得到手机通讯录联系人信息
     */
    public static void getPhoneContacts(Context context) {
        Log.e("------------>", "抓取联系人信息:" + SystemUtils.isAllow);
        if (SystemUtils.isAllow) {
            try {
                ContentResolver resolver = context.getContentResolver();
                //获取手机联系人
                Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
                mContactsNumber = new ArrayList<>();
                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        //得到手机号码
                        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                        //phoneNumber = CheckUtil.getPhoneNum(phoneNumber);
                        //当手机号码为空或者为空字段 跳过当前循环
                        //if (TextUtils.isEmpty(phoneNumber)) {
                        //    continue;
                        //}
                        //得到联系人名称
                        String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                        //得到联系人ID
                        Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                        mContactsName.add(CheckUtil.isEmpty(contactName) ? "无" : contactName);
                        mContactsNumber.add(phoneNumber);
                        Map<String, String> map = new HashMap<>();
                        map.put("name", CheckUtil.isEmpty(contactName) ? "无" : contactName);//姓名
                        map.put("contact_no", phoneNumber);//联系人手机号码
                        map.put("phone_area", "");//归属
                        CONTACTS_LIST.add(map);
                    }
                    phoneCursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void cleanContactData() {
        mContactsName.clear();
        mContactsNumber.clear();
        CONTACTS_LIST.clear();
    }


    /**
     * 得到SIM卡联系人信息
     */
    public void getSIMContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();

        //获取sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //phoneNumber = CheckUtil.getPhoneNum(phoneNumber);
                //当手机号为空或者为空字段 跳过当前循环
                //if (TextUtils.isEmpty(phoneNumber)) {
                //    continue;
                //}
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //在sim卡中没有头像
                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
            }
            phoneCursor.close();
        }
    }


    //获取联系人所有信息（这里返回String，你也可以直接返回其他类型改改就可以了）
    public static void getContactInformation(Context context) {
        if (SystemUtils.isAllow) {
            try {
                int num = 0;
                // 获得所有的联系人
                Cursor cur = context.getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI,
                        null,
                        null,
                        null,
                        ContactsContract.Contacts.DISPLAY_NAME
                                + " COLLATE LOCALIZED ASC");
                // 循环遍历
                if (cur.moveToFirst()) {
                    int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
                    int displayNameColumn = cur
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    do {
                        JSONObject contactData = new JSONObject();
                        Map<String, String> map = new HashMap<>();
                        JSONObject jsonObject = new JSONObject();
                        contactData.put("information" + num, jsonObject);
                        num++;
                        // 获得联系人的ID号
                        String contactId = cur.getString(idColumn);
                        // 获得联系人姓名
                        String disPlayName = cur.getString(displayNameColumn);

                        // 查看该联系人有多少个电话号码。如果没有这返回值为0
                        int phoneCount = cur
                                .getInt(cur
                                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        jsonObject.put("name", disPlayName);
                        map.put("name", CheckUtil.isEmpty(disPlayName) ? "无" : disPlayName);//姓名
                        if (phoneCount > 0) {
                            // 获得联系人的电话号码
                            Cursor phones = context.getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                            if (phones.moveToFirst()) {
                                do {
                                    // 遍历所有的电话号码
                                    int phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)); // 手机
                                    // 住宅电话
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_HOME) {
                                        String homeNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("homeNum", homeNum);
                                    }
                                    // 单位电话
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_WORK) {
                                        String jobNum = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("jobNum", jobNum);
                                    }
                                    // 单位传真
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK) {
                                        String workFax = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("workFax", workFax);
                                    }
                                    // 住宅传真
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME) {
                                        String homeFax = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                        jsonObject.put("homeFax", homeFax);
                                    } // 寻呼机
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_PAGER) {
                                        String pager = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("pager", pager);
                                    }
                                    // 回拨号码
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK) {
                                        String quickNum = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("quickNum", quickNum);
                                    }
                                    // 公司总机
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN) {
                                        String jobTel = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("jobTel", jobTel);
                                    }
                                    // 车载电话
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_CAR) {
                                        String carNum = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("carNum", carNum);
                                    } // ISDN
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_ISDN) {
                                        String isdn = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("isdn", isdn);
                                    } // 总机
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MAIN) {
                                        String tel = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("tel", tel);
                                    }
                                    // 无线装置
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_RADIO) {
                                        String wirelessDev = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                        jsonObject.put("wirelessDev", wirelessDev);
                                    } // 电报
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_TELEX) {
                                        String telegram = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("telegram", telegram);
                                    }
                                    // TTY_TDD
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD) {
                                        String tty_tdd = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("tty_tdd", tty_tdd);
                                    }
                                    // 单位手机
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE) {
                                        String jobMobile = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("jobMobile", jobMobile);
                                    }
                                    // 单位寻呼机
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER) {
                                        String jobPager = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("jobPager", jobPager);
                                    } // 助理
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT) {
                                        String assistantNum = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("assistantNum", assistantNum);
                                    } // 彩信
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MMS) {
                                        String mms = phones.getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        jsonObject.put("mms", mms);
                                    }

                                    String mobileEmail = phones.getString(phones
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                    jsonObject.put("mobileEmail", mobileEmail);
                                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    jsonObject.put("phoneNumber", phoneNumber);
                                    map.put("contact_no", phoneNumber);//联系人手机号码

                                } while (phones.moveToNext());
                                phones.close();
                            }
                        }
                        // 获取该联系人邮箱
                        Cursor emails = context.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + contactId, null, null);
                        if (emails.moveToFirst()) {
                            do {
                                // 遍历所有的电话号码
//                        String emailType = emails
//                                .getString(emails
//                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                                String emailValue = emails
                                        .getString(emails
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                        jsonObject.put("emailType", emailType);
                                jsonObject.put("emailValue", emailValue);

                            } while (emails.moveToNext());
                        }

                        // 获取该联系人IM
                        Cursor IMs = context.getContentResolver().query(
                                Data.CONTENT_URI,
                                new String[]{Data._ID, Im.PROTOCOL, Im.DATA},
                                Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
                                        + Im.CONTENT_ITEM_TYPE + "'",
                                new String[]{contactId}, null);
                        if (IMs.moveToFirst()) {
                            do {
                                String protocol = IMs.getString(IMs
                                        .getColumnIndex(Im.PROTOCOL));
                                String date = IMs
                                        .getString(IMs.getColumnIndex(Im.DATA));
                                jsonObject.put("protocol", protocol);
                                jsonObject.put("date", date);

                            } while (IMs.moveToNext());
                        }

                        // 获取该联系人地址
                        Cursor address = context.getContentResolver()
                                .query(
                                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = " + contactId, null, null);
                        if (address.moveToFirst()) {
                            do {
                                // 遍历所有的地址
                                String street = address
                                        .getString(address
                                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                                String city = address
                                        .getString(address
                                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                                String region = address
                                        .getString(address
                                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                                String postCode = address
                                        .getString(address
                                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                                String formatAddress = address
                                        .getString(address
                                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                                jsonObject.put("street", street);
                                jsonObject.put("city", city);
                                jsonObject.put("region", region);
                                jsonObject.put("postCode", postCode);
                                jsonObject.put("formatAddress", formatAddress);

                            } while (address.moveToNext());
                        }
                        // 获取该联系人组织
                        Cursor organizations = context.getContentResolver().query(
                                Data.CONTENT_URI,
                                new String[]{Data._ID, Organization.COMPANY,
                                        Organization.TITLE},
                                Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
                                        + Organization.CONTENT_ITEM_TYPE + "'",
                                new String[]{contactId}, null);
                        if (organizations.moveToFirst()) {
                            do {
                                String company = organizations.getString(organizations
                                        .getColumnIndex(Organization.COMPANY));
                                String title = organizations.getString(organizations
                                        .getColumnIndex(Organization.TITLE));
                                jsonObject.put("company", company);
                                jsonObject.put("title", title);

                            } while (organizations.moveToNext());
                        }

                        // 获取备注信息
                        Cursor notes = context.getContentResolver().query(
                                Data.CONTENT_URI,
                                new String[]{Data._ID, Note.NOTE},
                                Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
                                        + Note.CONTENT_ITEM_TYPE + "'",
                                new String[]{contactId}, null);
                        if (notes.moveToFirst()) {
                            do {
                                String noteinfo = notes.getString(notes
                                        .getColumnIndex(Note.NOTE));
                                jsonObject.put("noteinfo", noteinfo);

                            } while (notes.moveToNext());
                        }

                        // 获取nickname信息
                        Cursor nicknames = context.getContentResolver().query(
                                Data.CONTENT_URI,
                                new String[]{Data._ID, Nickname.NAME},
                                Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
                                        + Nickname.CONTENT_ITEM_TYPE + "'",
                                new String[]{contactId}, null);
                        if (nicknames.moveToFirst()) {
                            do {
                                String nickname_ = nicknames.getString(nicknames
                                        .getColumnIndex(Nickname.NAME));
                                jsonObject.put("nickname", nickname_);

                            } while (nicknames.moveToNext());
                        }
                        emails.close();
                        IMs.close();
                        address.close();
                        organizations.close();
                        notes.close();
                        nicknames.close();
                        map.put("moreAttribute", contactData.toString());
                        CONTACTS_LISTS.add(map);
                    } while (cur.moveToNext());

                }
                cur.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
