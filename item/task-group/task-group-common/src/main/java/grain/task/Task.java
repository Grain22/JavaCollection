package grain.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author wulifu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Task {
    public static final int PRIORITY_TOP = 9;
    public static final int PRIORITY_BOTTOM = 1;
    Integer taskId;
    Object info;
    int priority = 5;
    String addedDate;
    Integer taskLong;

    public void changePriority(int to) {
        if (to > PRIORITY_TOP) {
            this.priority = PRIORITY_TOP;
        } else if (to < PRIORITY_BOTTOM) {
            this.priority = PRIORITY_BOTTOM;
        } else {
            this.priority = to;
        }
    }

    public void increasePriority() {
        if (this.priority < PRIORITY_TOP) {
            this.priority++;
        }
    }

    public void decreasePriority() {
        if (this.priority > PRIORITY_BOTTOM) {
            this.priority--;
        }
    }
}
