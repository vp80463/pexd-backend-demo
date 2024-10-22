/******************************************************************************/
/* SYSTEM     : A1 STREAM                                                        */
/*                                                                            */
/* SUBSYSTEM  : MDM                                                           */
/******************************************************************************/
package com.a1stream.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StopWatch;

/**
 *
 * @author Dong Zhen
 *  (Rev.)		(Date)     	(Name)        (Comment)
 *  1.0    		Aug 27, 2014    	Lin zhanwang       New making
 */
public abstract class TimerUtil {

  private static final Log logger = LogFactory.getLog(TimerUtil.class);

  /**
   * @param timer
   * @param log
   * @param process
   * @param params
   */
  public static void startProcess(StopWatch timer, Log log, String process, Object ... params) {

    Log use = getLog(log);
    String pname = String.format("********Start " + process + "********", params );
    if(use.isInfoEnabled()) {
      use.info(pname);
    }
    timer.start(String.format(process, params ));
  }

  /**
   * @param timer
   */
  public static void endProcess(StopWatch timer) {

    endProcess(timer, null);
  }

  /**
   * @param timer
   */
  public static void endProcess(StopWatch timer, Log log) {

    Log use = getLog(log);
    timer.stop();

    if(use.isInfoEnabled()) {
      use.info(String.format("********End of Cost Usage Process, during time: " + timer.getTotalTimeMillis() + "ms********", timer.lastTaskInfo().getTaskName()));
    }
  }

  /**
   * @param taskTimer
   * @param task
   * @param params
   */
  public static void startTask(StopWatch taskTimer, String task, Object ... params) {

     taskTimer.start(String.format(task, params ));
  }

  /**
   * @param taskTimer
   */
  public static void endTask(StopWatch taskTimer) {

     endTask(taskTimer, null);
  }

  /**
   * @param taskTimer
   * @param comment
   */
  public static void endTask(StopWatch taskTimer, String comment, Object ... commentArgs) {

     endTask(taskTimer, null, comment, commentArgs);
  }

  /**
   * @param taskTimer
   * @param log
   */
  public static void endTask(StopWatch taskTimer, Log log) {

    endTask(taskTimer, log, null);
  }


  /**
   * @param taskTimer
   * @param log
   * @param comment
   * @param commentArgs
   */
  public static void endTask(StopWatch taskTimer, Log log, String comment, Object ... commentArgs) {

     taskTimer.stop();
     Log use =  getLog(log);
     if(use.isInfoEnabled()) {
       String cm = "";
       if(comment !=null) {
         cm = ",comment:" + String.format(comment, commentArgs);
       }
       use.info("Task '" + taskTimer.lastTaskInfo().getTaskName() + "'" + cm +", during time:" + taskTimer.lastTaskInfo().getTimeMillis() + "ms");
     }
  }

  /**
   * @param log
   * @return
   */
  private static Log getLog(Log log) {
      return (log == null) ? logger : log;
  }
}