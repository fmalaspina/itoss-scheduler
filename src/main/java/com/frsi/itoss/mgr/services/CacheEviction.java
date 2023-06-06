package com.frsi.itoss.mgr.services;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class CacheEviction {
//
//	@Autowired
//	CacheManager cacheManager;
//
//	@Scheduled(fixedRate = 6000)
//	public void evictAllcachesAtIntervals() {
//		evictAllCaches();
//	}
//
//	public void evictAllCaches() {
//		log.info("CACHE NAME: ->");
//		cacheManager.getCacheNames().stream().forEach(cacheName -> {
//			log.info("CACHE NAME: ->" + cacheName);
//			cacheManager.getCache(cacheName).clear();
//		});
//	}
}