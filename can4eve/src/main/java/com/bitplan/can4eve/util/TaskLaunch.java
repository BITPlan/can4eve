/**
 *
 * This file is part of the https://github.com/BITPlan/can4eve open source project
 *
 * Copyright 2017 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.can4eve.util;

import java.util.concurrent.Callable;

import javafx.concurrent.Task;

/**
 * this is a utility task to launch tasks with lambda expressions
 * 
 * @author wf
 *
 */
public class TaskLaunch<T> {

  Thread thread;
  Task<T> task;
  Callable<T> callable;
  Throwable throwable;
  Class<T> clazz;

  public Thread getThread() {
    return thread;
  }

  public void setThread(Thread thread) {
    this.thread = thread;
  }

  public Task<T> getTask() {
    return task;
  }

  public void setTask(Task<T> task) {
    this.task = task;
  }

  public Callable<T> getCallable() {
    return callable;
  }

  public void setCallable(Callable<T> callable) {
    this.callable = callable;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public void setClazz(Class<T> clazz) {
    this.clazz = clazz;
  }

  /**
   * construct me from a callable
   * 
   * @param callable
   */
  public TaskLaunch(Callable<T> callable, Class<T> clazz) {
    this.callable = callable;
    this.task = task(callable);
    this.clazz = clazz;
  }

  /**
   * 
   * @param callable
   * @return the new task
   */
  public static <T> Task<T> task(Callable<T> callable) {
    Task<T> task = new Task<T>() {
      @Override
      public T call() throws Exception {
        return callable.call();
      }
    };
    return task;
  }

  /**
   * start
   */
  public void start() {
    thread = new Thread(task);
    thread.start();
  }

  /**
   * start the given callable
   * @param callable
   * @param clazz - the return Type class
   * @return - the launch result
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static TaskLaunch start(Callable<?> callable, Class<?> clazz) {
    TaskLaunch<?> launch = new TaskLaunch(callable, clazz);
    launch.start();
    return launch;
  }
  
  /**
   * call a void
   * @param callable
   * @return the TaskLaunch
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static TaskLaunch start(Callable<Void> callable) {
    TaskLaunch<Void> launch = new TaskLaunch(callable, Void.class);
    launch.start();
    return launch;
  }

}
