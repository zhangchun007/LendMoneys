package com.haiercash.gouhua.fragments.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.bean.CollectInfo;
import com.app.haiercash.base.db.DbUtils;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.FileUtils;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.image.PhotographUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SoftHideKeyBoardUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.ModelBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.UtilPhoto;
import com.haiercash.gouhua.widget.FlowRadioGroup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/9/21
 * 描    述：我的留言+意见反馈
 * 修订历史：
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_FEEDBACK)
public class FeedBackFragment extends BaseFragment {
    private String TITLE_NAME = "意见反馈";
    @BindView(R.id.et_feed_content)
    EditText et_feed_content;
    @BindView(R.id.tv_sum_words)
    TextView tv_sum_words;
    @BindView(R.id.iv_image1)
    ImageView ivImage1;
    @BindView(R.id.iv_image_close1)
    ImageView ivImageClose1;
    @BindView(R.id.iv_image2)
    ImageView ivImage2;
    @BindView(R.id.iv_image_close2)
    ImageView ivImageClose2;
    @BindView(R.id.iv_image3)
    ImageView ivImage3;
    @BindView(R.id.iv_image_close3)
    ImageView ivImageClose3;

    //private ImageView ivImage;
    @BindView(R.id.frg_type)
    FlowRadioGroup frgType;
    private String dictTypeCode = null;
    private List<String> images = new ArrayList<>();
    //    private ShowPhotoPop photoPop;
    private String titleRightText = "提交";
    private int rowNumbers = 0;
    private int rbWidth = 0;
    private String tempTxtFilePath;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_feedback;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle(TITLE_NAME);
        SoftHideKeyBoardUtil.assistActivity(mActivity);
        mActivity.setBarRightText(titleRightText, UiUtil.getColor(R.color.colorPrimary), v -> submitFeedBack());
        mActivity.setBarLeftImage(0, v -> onBackPressed());
        //删除历史压缩照片
        FileUtils.deleteFile(new File(FileUtils.getExternalFilesDir() + FileUtils.PATH_FEEDBACK));
        if (getArguments() != null) {
            String feedbackPath = getArguments().getString("feedbackPath");
            if (!CheckUtil.isEmpty(feedbackPath)) {
                images.add(feedbackPath);
                resetInitImage();
            }
        }
        String fSwitch = SpHp.getOther(SpKey.FEEDBACK_SWITCH, "Y");
        mView.findViewById(R.id.v_line).setVisibility("Y".equals(fSwitch) ? View.VISIBLE : View.GONE);
        mView.findViewById(R.id.tv_screenshot_title).setVisibility("Y".equals(fSwitch) ? View.VISIBLE : View.GONE);
        mView.findViewById(R.id.ll_screenshot_img).setVisibility("Y".equals(fSwitch) ? View.VISIBLE : View.GONE);

        Map<String, String> map = new HashMap<>();
        map.put("parentCode", "feedbackBizType");
        netHelper.getService(ApiUrl.URL_FEEDBACK_TYPE, map);

    }


    private void rbWidthSum() {
        //总共可容纳的宽度=屏幕宽度-两边padding的值
        int width = SystemUtils.getDeviceWidth(mActivity) - UiUtil.dip2px(mActivity, 10) * 2;
        //默认radioButton最小宽度
        int minWidth = UiUtil.dip2px(mActivity, 80);
        //radioButton之间的间隙即margin值
        int offset = UiUtil.dip2px(mActivity, 12);
        //radioButton的连框宽度
        int offsetWidth = UiUtil.dip2px(mActivity, 1);
        //一行可容纳radioButton的个数
        rowNumbers = (width + offset) / (offset + minWidth + 2 * offsetWidth);
        //真实的每个按钮的宽度
        rbWidth = (width - (rowNumbers - 1) * offset - 2 * offsetWidth * rowNumbers) / rowNumbers;
    }

    @OnClick({R.id.iv_image1, R.id.iv_image_close1, R.id.iv_image2, R.id.iv_image_close2, R.id.iv_image3, R.id.iv_image_close3})
    void onViewOnClick(View view) {
        String filePath = String.valueOf(view.getTag(R.id.imageIdTag));
        switch (view.getId()) {
            case R.id.iv_image1:
            case R.id.iv_image2:
            case R.id.iv_image3:
                if (CheckUtil.isEmpty(filePath)) {
                    //选择照片、拍照
                    //ivImage = (ImageView) view;
                    //photographImageName = "image_" + System.currentTimeMillis() + ".jpg";
                    new UtilPhoto(2).showDialog(mActivity, "");
                }
                //else {
                //    //查看图片
                //    if (photoPop == null) {
                //        photoPop = new ShowPhotoPop(mActivity, null);
                //    }
                //    photoPop.updateData(images, images.indexOf(filePath));
                //    photoPop.showAtLocation(view);
                //}
                break;
            case R.id.iv_image_close1:
            case R.id.iv_image_close2:
            case R.id.iv_image_close3:
                //删除对应的文件
                //FileUtils.deleteFile(new File(filePath));
                images.remove(filePath);
                resetInitImage();
                break;
            default:
                break;
        }
    }

    @OnTouch(R.id.et_feed_content)
    boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            //通知父控件不要干扰
            view.getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            //通知父控件不要干扰
            view.getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            view.getParent().requestDisallowInterceptTouchEvent(false);
        }

        return false;
    }

    /**
     * 提交反馈
     */
    private void submitFeedBack() {
        if (CheckUtil.isEmpty(et_feed_content.getText()) || et_feed_content.getText().toString().length() <= 3) {
            UiUtil.toast("字数太少，再多说几句吧");
            return;
        }
        RadioButton radioButton = frgType.findViewById(frgType.getCheckedRadioButtonId());
        if (radioButton != null) {
            dictTypeCode = String.valueOf(radioButton.getTag());
        }
        if (CheckUtil.isEmpty(dictTypeCode)) {
            UiUtil.toast("请选择反馈类型");
            return;
        }
        showDialog("确认要反馈吗？", "取消", "确定", (dialog, which) -> {
            if (which == 2) {
                imageCompressThenSubmit();
            }
        }).setButtonTextColor(2, R.color.colorPrimary);
    }


    /**
     * 保存数据记录
     */
    private String saveCollet() {
        BufferedWriter bw = null;
        try {
            List<CollectInfo> list = DbUtils.getAppDatabase().collectInfoDao().getAllCollect();
            String outFile = FileUtils.getExternalFilesDir() + FileUtils.PATH_FEEDBACK + File.separator + System.currentTimeMillis() + "_collet.txt";
            File file = new File(outFile).getParentFile();
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                System.out.println("File mkdirs:" + mkdirs);
            }

            bw = new BufferedWriter(new FileWriter(new File(outFile)));
            for (CollectInfo info : list) {
                bw.write(info.toString());
            }

            return outFile;
        } catch (Exception e) {
            System.out.println("----step:Exception" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    /**
     * 压缩图片后提交图片
     * 参考资料：https://blog.csdn.net/u013928412/article/details/80358597
     */
    private void imageCompressThenSubmit() {
        showProgress(true);
        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        executorService.submit(() -> {
            Map<String, String> filePaths = new IdentityHashMap<>();
            for (String filePath : images) {
                String compressPath = FileUtils.compressToMaxSize(50, FileUtils.PATH_FEEDBACK, filePath);
                if (compressPath != null) {
                    //需要用new String("files") 保证map中可以相同的key对应不同的value
                    filePaths.put(new String("files"), compressPath);
                }
            }
            tempTxtFilePath = saveCollet();
            String path = FileUtils.toZip(tempTxtFilePath);
            //压缩完成后删除临时文件
            FileUtils.deleteFile(new File(tempTxtFilePath));
            tempTxtFilePath = path;
            if (BuildConfig.IS_RELEASE) {
                filePaths.put("zip", path);
            } else {
                filePaths.put("zip", Logger.getFilePath());
            }
            Map<String, String> map = new HashMap<>();
            String name = SpHp.getUser(SpKey.USER_CUSTNAME);
            String mobile = SpHp.getUser(SpKey.USER_MOBILE);
            String deviceId = SystemUtils.getDeviceID(mActivity);
            map.put("feedbackType", "GOU_HUA");
            map.put("feedbackBizType", dictTypeCode);
            map.put("feedbackContent", et_feed_content.getText().toString());
            if (!CheckUtil.isEmpty(name)) {
                map.put("userName", RSAUtils.encryptByRSANew(name));
            }
            if (!CheckUtil.isEmpty(mobile)) {
                map.put("userMobile", RSAUtils.encryptByRSANew(mobile));
            }
            map.put("deviceId", RSAUtils.encryptByRSANew(deviceId));
            map.put("userId", AppApplication.userid);
            map.put("isRsa", "Y");
            netHelper.postService(ApiUrl.URL_FEEDBACK, map, filePaths);
        });
    }

    private void initTextString(String length) {
        SpannableStringBuilder sp = new SpannableStringBuilder(length);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#f6691c")), 0, length.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.append("/300");
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), length.length(), sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_sum_words.setText(sp);
    }

    @OnTextChanged(value = R.id.et_feed_content, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
        if (s.toString().length() > 300) {
            showDialog("字数太多，请精简到300字以内", "确定", null, null);
        }
        initTextString(String.valueOf(s.toString().length()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            String filePath = null;
            switch (requestCode) {
                case PhotographUtils.THECAMERA://拍照
                    //if (!CheckUtil.isEmpty(photographImageName)) {
                    //    filePath = PhotographUtils.getCameraFile(photographImageName).getPath();
                    //}
                    break;
                case PhotographUtils.PHOTOALBUM://相册
                    filePath = FileUtils.getFilePathByUri(mActivity, data.getData());
                    break;
                default:
                    break;
            }
            if (filePath != null) {
                if (images.contains(filePath)) {
                    UiUtil.toast("该图片已经存在");
                    return;
                }
                images.add(filePath);
                resetInitImage();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        //if (photoPop != null && photoPop.isShowing()) {
        //    photoPop.dismiss();
        //    return true;
        //} else
        if (!CheckUtil.isEmpty(et_feed_content.getText()) || images.size() > 0) {
            showDialog("您还没有提交，确认退出吗？", "退出", "继续编辑", (dialog, which) -> {
                if (which == 1) {
                    mActivity.finish();
                }
            });
        } else {
            mActivity.finish();
        }
        return false;
    }

    @Override
    public void onSuccess(Object response, String url) {
        showProgress(false);
        if (ApiUrl.URL_FEEDBACK_TYPE.equals(url)) {
            rbWidthSum();
            List<ModelBean> dict = JsonUtils.fromJsonArray(response, ModelBean.class);
            for (ModelBean bean : dict) {
                if (bean != null && !CheckUtil.isEmpty(bean.getDictName())) {
                    addFeedBackRadioButton(bean.getDictName(), bean.getDictCode());
                }
            }
        } else {
            UiUtil.toast("提交成功");
            FileUtils.deleteFile(new File(tempTxtFilePath));
            mActivity.finish();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
    }

    /**
     * 刷新当前选择图片的布局
     */
    private void resetInitImage() {
        ivImage1.setTag(R.id.imageIdTag, images.size() <= 0 ? null : images.get(0));
        ivImageClose1.setVisibility(images.size() <= 0 ? View.GONE : View.VISIBLE);
        ivImageClose1.setTag(R.id.imageIdTag, images.size() <= 0 ? null : images.get(0));
        ivImage2.setVisibility(images.size() < 1 ? View.GONE : View.VISIBLE);
        ivImage2.setTag(R.id.imageIdTag, images.size() <= 1 ? null : images.get(1));
        ivImageClose2.setVisibility(images.size() <= 1 ? View.GONE : View.VISIBLE);
        ivImageClose2.setTag(R.id.imageIdTag, images.size() <= 1 ? null : images.get(1));
        ivImage3.setVisibility(images.size() < 2 ? View.GONE : View.VISIBLE);
        ivImage3.setTag(R.id.imageIdTag, images.size() <= 2 ? null : images.get(2));
        ivImageClose3.setVisibility(images.size() <= 2 ? View.GONE : View.VISIBLE);
        ivImageClose3.setTag(R.id.imageIdTag, images.size() <= 2 ? null : images.get(2));
        switch (images.size()) {
            case 0:
                ivImage1.setImageResource(R.drawable.src_feedback_1);
                ivImage2.setImageResource(R.drawable.src_feedback_2);
                ivImage3.setImageResource(R.drawable.src_feedback_3);
                break;
            case 1:
                GlideUtils.loadPicFormLocal(mActivity, images.get(0), ivImage1, 0);
                ivImage2.setImageResource(R.drawable.src_feedback_2);
                ivImage3.setImageResource(R.drawable.src_feedback_3);
                break;
            case 2:
                GlideUtils.loadPicFormLocal(mActivity, images.get(0), ivImage1, 0);
                GlideUtils.loadPicFormLocal(mActivity, images.get(1), ivImage2, 0);
                ivImage3.setImageResource(R.drawable.src_feedback_3);
                break;
            case 3:
                GlideUtils.loadPicFormLocal(mActivity, images.get(0), ivImage1, 0);
                GlideUtils.loadPicFormLocal(mActivity, images.get(1), ivImage2, 0);
                GlideUtils.loadPicFormLocal(mActivity, images.get(2), ivImage3, 0);
                break;
            default:
                break;
        }
    }

    /**
     * 添加反馈类型的标签
     */
    private void addFeedBackRadioButton(CharSequence text, String codeTag) {
        int padding = UiUtil.dip2px(mActivity, 10);
        int margin = UiUtil.dip2px(mActivity, 10);
        RadioButton radioButton = new RadioButton(mActivity);
        radioButton.setButtonDrawable(null);
        radioButton.setBackgroundResource(R.drawable.rb_feedback_type);
        radioButton.setText(text);
        radioButton.setTag(codeTag);
        radioButton.setGravity(Gravity.CENTER);
        radioButton.setMinWidth(UiUtil.dip2px(mActivity, 80));
        radioButton.setTextColor(ContextCompat.getColorStateList(mActivity, R.color.tab_selector));
        radioButton.setPadding(padding, padding, padding, padding);
        if (rbWidth > 0) {
            radioButton.setWidth(rbWidth);
        }
        frgType.addView(radioButton);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) radioButton.getLayoutParams();
        if (rbWidth > 0) {
            params.setMargins(0, margin, frgType.getChildCount() % rowNumbers == 0 ? 0 : margin, 0);
        } else {
            params.setMargins(0, margin, margin, 0);
        }
    }
}
