package com.gionee.gntravel.entity;

import java.io.Serializable;


public class WeatherLocal implements Serializable{
		private String city;
		private long lastUpdateTime;
		private int showIndex;
		private WeatherBean weatherBean;
		/**
		 * 标志位，用来标示是否需要更新
		 */
		private boolean shouldUpdate;

		public boolean isShouldUpdate() {
			return shouldUpdate;
		}

		public void setShouldUpdate(boolean shouldUpdate) {
			this.shouldUpdate = shouldUpdate;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public long getLastUpdateTime() {
			return lastUpdateTime;
		}

		public void setLastUpdateTime(long lastUpdateTime) {
			this.lastUpdateTime = lastUpdateTime;
		}

		public int getShowIndex() {
			return showIndex;
		}

		public void setShowIndex(int showIndex) {
			this.showIndex = showIndex;
		}

		public WeatherBean getWeatherBean() {
			return weatherBean;
		}

		public void setWeatherBean(WeatherBean weatherBean) {
			this.weatherBean = weatherBean;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((city == null) ? 0 : city.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WeatherLocal other = (WeatherLocal) obj;
			if (city == null) {
				if (other.city != null)
					return false;
			} else if (!city.equals(other.city))
				return false;
			return true;
		}

}
