package com.ctrip.openapi.java.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.gionee.gntravel.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ImageUtil {
	/**
	 * 请求从相机获取图片
	 */
	public static final int REQUEST_CAMERY = 99;
	/**
	 * 请求从相册获取图片
	 */
	public static final int REQUEST_GARRERY = 98;
	/**
	 * 请求缩放图片
	 */
	public static final int REQUEST_ZOOM = 97;

	/**
	 * 从相机获取图片的uri
	 */
	private Uri cameraUri;

	private static ImageUtil loader;

	private ImageLoader imageLoader;

	private File cacheDir;

	private HashCodeFileNameGenerator generator;

	private Context context;
	private File zoomOutFile;

	public static ImageUtil getInstance(Context context) {
		if (loader == null) {
			loader = new ImageUtil(context);
		}
		return loader;
	}

	/**
	 * 根据图片的url删除图片缓存
	 * 
	 * @author Roy
	 * @date 2013-1-14
	 * @param url
	 */
	public void clearDiscCache(String url) {
		File diskFile = findCacheFileByUrl(url);
		if (diskFile != null) {
			boolean suc = diskFile.delete();
			if (suc) {
				Log.e("clearDiscCache", " 删除：" + url + "，对应文件：" + diskFile.getName() + " 成功。");
			} else {
				Log.e("clearDiscCache", " 删除：" + url + "，对应文件：" + diskFile.getName() + " 失败。");
			}
		}
	}

	/**
	 * 没有文件，返回null
	 * 
	 * @author Roy
	 * @date 2013-1-16
	 * @param url
	 * @return
	 */
	public File findCacheFileByUrl(String url) {
		File cacheFolder = StorageUtils.getCacheDirectory(context);
		if (!cacheFolder.exists()) {
			return null;
		}
		if (url == null) {
			return null;
		}
		String name = generator.generate(url);
		File diskFile = new File(cacheFolder, name);
		Log.e("findCacheFileByUrl: ", diskFile.getAbsolutePath() + ",file is " + (diskFile.exists() ? "" : "not") + " exist!");
		if (!diskFile.exists()) {
			return null;
		}
		return diskFile;
	}

	/**
	 * 弹出：从"相册获取"或者"拍张"的对话框。
	 * 
	 * @author Roy
	 * @date 2012-12-3 下午1:54:21
	 */
	public void showSelectPhotoDialog(final Activity context) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("请选择");
		b.setItems(new String[] { "拍照", "从相册获取" }, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					try {
						cameraUri = openCamera(context);
					} catch (Exception e) {
//						toast.showShort("无法打开相机。");
						e.printStackTrace();
					}
					break;
				case 1:
					try {
						openGarrery(context);
					} catch (Exception e) {
//						toast.showShort("无法打开相册。");
						e.printStackTrace();
					}
					break;
				}
			}
		});
		b.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		b.create().show();
	}

	/**
	 * 解析缩放结果，并且得到位图
	 * 
	 * @author Roy
	 * @param zoomOutFile2
	 * @date 2012-11-25 下午4:30:07
	 * @param data
	 *            onActivityResult Intent
	 * @param inSampleSize
	 *            缩小倍数
	 * @return
	 */
	public Bitmap parseZoomedResult(File file, int inSampleSize) {
		if (file == null) {
			return null;
		}
		// Bundle extras = data.getExtras();
		Bitmap bm = null;
		// if (extras != null) {
		// Bitmap photo = extras.getParcelable("data");
		// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// photo.compress(CompressFormat.JPEG, 90, outputStream);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = inSampleSize;
		bm = BitmapFactory.decodeFile(zoomOutFile.getAbsolutePath(), opts);
		// bm = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0,
		// outputStream.size(), opts);
		// }
		return bm;
	}

	/**
	 * 解析相册获取结果，得到相册图片的地址
	 * 
	 * @author Roy
	 * @date 2012-11-25 下午4:26:21
	 * @param data
	 *            onActivityResult Intent
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public void parseGarreryResult(Activity context, Intent data) {
		if (data == null) {
			return;
		}
		Uri uri = data.getData();

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(uri, projection, null, null, null);
		if (cursor == null)
			return;
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		File file = new File(path);
		cameraUri = Uri.fromFile(file);
	}

	/**
	 * 保存bm到本地
	 * 
	 * @author Roy
	 * @date 2012-12-3 下午1:43:34
	 * @param bm
	 * @return
	 */
	public File saveBitmap2Storate(Bitmap bm) {
		if (bm == null) {
			return null;
		}
		File file = new File(StorageUtils.getCacheDirectory(context), System.currentTimeMillis() + "_cuted" + ".jpg");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bm.compress(CompressFormat.JPEG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return file;
	}

	/**
	 * 打开程序 缩放图片,RequestCode={@link #REQUEST_ZOOM}
	 * 
	 * @author Roy
	 * @date 2012-12-3 下午1:47:21
	 * @param uri
	 * @throws Exception
	 *             没有缩放图片的程序。
	 */
	public void openZoom(Activity context, Uri uri, int maxWidth, int maxHeight) throws Exception {
		if (uri == null) {
			return;
		}
		zoomOutFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
		if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			throw new RuntimeException("无法剪切图片，请挂载SD卡！");
		}
		// Intent intent = new Intent("com.android.camera.action.CROP");
		// intent.setDataAndType(uri, "image/*");
		// intent.putExtra("crop", "true");
		// intent.putExtra("aspectX", 1);
		// intent.putExtra("aspectY", 1);
		// intent.putExtra("outputX", maxWidth);
		// intent.putExtra("outputY", maxHeight);
		// intent.putExtra("scale", true);
		// intent.putExtra("noFaceDetection", true);
		// intent.putExtra("return-data", false);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", maxWidth);
		intent.putExtra("outputY", maxHeight);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(zoomOutFile));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		context.startActivityForResult(intent, REQUEST_ZOOM);
	}

	/**
	 * 打开 相机获取图片,RequestCode={@link #REQUEST_CAMERY}
	 * 
	 * @author Roy
	 * @date 2012-11-25 下午4:22:31
	 * @throws Exception
	 *             无法打开相机
	 */
	public Uri openCamera(Activity context) throws Exception {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File folder = StorageUtils.getCacheDirectory(context);
		cameraUri = Uri.fromFile(new File(folder, System.currentTimeMillis() + ".jpg"));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		context.startActivityForResult(intent, REQUEST_CAMERY);
		return cameraUri;
	}

	public Uri getCameraUri() {
		return cameraUri;
	}

	/**
	 * 打开 相册获取图片,RequestCode={@link #REQUEST_GARRERY}
	 * 
	 * @author Roy
	 * @date 2012-11-25 下午4:22:22
	 * @throws Exception
	 *             没有相册
	 */
	public void openGarrery(Activity context) throws Exception {
		Intent intent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		context.startActivityForResult(intent, REQUEST_GARRERY);
	}

	public int getLoadingImg() {
		return R.drawable.defaltimg7354;
	}

	public void clearCache() {
		imageLoader.clearDiscCache();
	}

	public int getLoadingFailImg() {
		return R.drawable.defaltimg7354;
	}

	public File getCacheFolder() {
		return StorageUtils.getCacheDirectory(context);
	}

	public void display(String url, ImageView iv, ImageLoadingListener l) {
		display(url, iv, l, 200, true);
	}

	public void display(String url, ImageView iv, final ImageLoadingListener l, int showDelay, boolean showLoadingImg) {
		imageLoader.displayImage(url, iv, getDisplayOption(showDelay, showLoadingImg, getLoadingImg(), getLoadingFailImg()), l);
	}

	public void display(String url, ImageView iv, int loading, int fail) {
		display(url, iv, loading, fail, null);
	}

	public void display(String url, ImageView iv, int loading, int fail, ImageLoadingListener l) {
		imageLoader.displayImage(url, iv, getDisplayOption(0, false, loading, fail), l);
	}

	public Bitmap getBitmFromAccess(Context context, String name) throws IOException {
		if (name == null) {
			return null;
		}
		InputStream is = context.getResources().getAssets().open(name);
		Bitmap bm = BitmapFactory.decodeStream(is);
		return bm;
	}

	public File getCacheImg(String url) {
		if (url == null) {
			return null;
		}
		return new File(cacheDir, generator.generate(url));
	}

	public Bitmap getCacheBitmap(String url) {
		File file = getCacheImg(url);
		if (!file.exists()) {
			return null;
		}
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}

	private ImageUtil(Context context) {
		this.context = context;

		DisplayImageOptions options = getDisplayOption(200, true, getLoadingImg(), getLoadingFailImg());

		generator = new HashCodeFileNameGenerator();

		cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(480, 800)
				// default = device screen dimensions
				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75).threadPoolSize(5).tasksProcessingOrder(QueueProcessingType.LIFO)
				.threadPriority(Thread.NORM_PRIORITY - 1).memoryCache(new FIFOLimitedMemoryCache(3))
				.discCache(new UnlimitedDiscCache(cacheDir)).denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(generator) // default
				.defaultDisplayImageOptions(options).build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);

//		toast = new DDToast(context);
	}

	public HashCodeFileNameGenerator getNameGenerator() {
		return generator;
	}

	public DisplayImageOptions getDisplayOption(int delayShow, boolean showLoadingImg, int loadingImage, int failImage) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().showImageForEmptyUri(loadingImage)
				.showStubImage(loadingImage).showImageOnFail(failImage).delayBeforeLoading(delayShow).cacheInMemory().cacheOnDisc()
				.imageScaleType(ImageScaleType.NONE) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.displayer(new SimpleBitmapDisplayer()); // default

		if (showLoadingImg) {
			builder.resetViewBeforeLoading().showStubImage(getLoadingImg());
		}
		return builder.build();
	}

	public void parseResult(Activity mContext, int requestCode, int resultCode, Intent data, int maxWidth, int maxHeight, ImageListener l) {
		if (l == null) {
			return;
		}
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_GARRERY) {
				parseGarreryResult(mContext, data);
				try {
					openZoom(mContext, getCameraUri(), maxWidth, maxHeight);
				} catch (Exception e) {
					e.printStackTrace();
					l.onFail(new Exception("添加图片失败。CODE:0001", e));
				}
			}
			if (requestCode == REQUEST_CAMERY) {
				try {
					openZoom(mContext, getCameraUri(), maxWidth, maxHeight);
				} catch (Exception e) {
					e.printStackTrace();
					// toast.showShort("添加图片失败。CODE:0002");
					l.onFail(new Exception("添加图片失败。CODE:0002", e));
				}
			}
			if (requestCode == REQUEST_ZOOM) {
				try {
					Bitmap bm = parseZoomedResult(zoomOutFile, 1);
					// File cutedFile = saveBitmap2Storate(bm);
					// 退出就删除它
					zoomOutFile.deleteOnExit();
					// 加到视图上
					if (zoomOutFile.isFile() && zoomOutFile.exists() && bm != null) {
						// toast.showShort("REQUEST_ZOOM加载到图片了！");
						l.onSuc(zoomOutFile, bm);
					} else {
						l.onFail(new Exception("添加图片失败。CODE:0003"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					l.onFail(new Exception("添加图片失败。CODE:0004", e));
				}
			}
		} else {
		}
	}

	public interface ImageListener {
		void onSuc(File file, Bitmap bm);

		void onFail(Exception ex);
	}

	public Bitmap getBitmap(File mFile) {
		return BitmapFactory.decodeFile(mFile.getAbsolutePath());
	}
}
