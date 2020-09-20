package com.github.duanjiefei.lib_common_ui.recyclerview;

/**
 *
 */
public interface ItemViewDelegate<T> {

  int getItemViewLayoutId();

  boolean isForViewType(T item, int position);

  void convert(ViewHolder holder, T t, int position);
}
