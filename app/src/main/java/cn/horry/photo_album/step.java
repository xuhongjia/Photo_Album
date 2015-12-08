package cn.horry.photo_album;

/**
 * Created by Administrator on 2015/12/8.
 */
public class step {

    /**
     * title : 背部舒缓
     * description : 柔、贴、沉的手法让你全身心放松，帮助面部护理达到最佳吸收效果，检测身体哪里出现不适并给予相对应的舒缓放松，给身体减压，释放情绪，拥有好心情
     * step_time : 0
     * step_pic : http://imeitouyi.b0.upaiyun.com/uploadfiles/steps/step1.jpg
     * small_pic : http://imeitouyi.b0.upaiyun.com/uploadfiles/steps/step1.jpg!step
     */

    private String title;
    private String description;
    private String step_time;
    private String step_pic;
    private String small_pic;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStep_time(String step_time) {
        this.step_time = step_time;
    }

    public void setStep_pic(String step_pic) {
        this.step_pic = step_pic;
    }

    public void setSmall_pic(String small_pic) {
        this.small_pic = small_pic;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStep_time() {
        return step_time;
    }

    public String getStep_pic() {
        return step_pic;
    }

    public String getSmall_pic() {
        return small_pic;
    }
}
