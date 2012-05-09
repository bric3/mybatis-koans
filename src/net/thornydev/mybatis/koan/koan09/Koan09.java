package net.thornydev.mybatis.koan.koan09;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

import net.thornydev.mybatis.koan.domain.City;
import net.thornydev.mybatis.koan.domain.Country;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

// Koan09 finally gets us to more than one table.  We learn about how to
// use MyBatis ResultMaps to map table column names to object property
// names or constructor arguments.
// 
// ResultMaps can be used for single table queries or multiple table queries.
// 
// Before moving to ResultMaps mapping multiple tables to domain objects,
// we will start with the good old country table to Country object mapping
// but this time use an explicit MyBatis ResultMap, rather than the implicit
// one we've been using thus far.
// 
// Next we introduce the City object that has a "has-one" (or "belongs to")
// association with the Country object. We will meditate on two ways to query 
// the city table to also bring back its corresponding Country object from
// the country table.  In one case ("nested selects") we do 1 query on the
// city table and N queries on the Country table, where N is the number of
// cities retrieved.  This is rather inefficient.
// 
// By doing a relational join, we can do a single query.  MyBatis uses what
// it calls "nested results" to create City-Country object duos from a single
// query using joins.
// 
// FIXME: more here ...
public class Koan09 {

	static SqlSessionFactory sessionFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String resource = "net/thornydev/mybatis/koan/koan09/koan09-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		inputStream.close();
	}

	@Test
	public void learnToUseResultMapForSingleTableQuery() {
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			Koan09Mapper mapper = session.getMapper(Koan09Mapper.class);
			Country c = mapper.getCountryById(33);
			
			assertEquals(33, c.getId());
			assertEquals("Finland", c.getCountry());
			assertNotNull(c.getLastUpdate());
			
		} finally {
			if (session != null) session.close();
		}
	}

	@Test
	public void learnToUseResultMapForTwoTableAssociation() {
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			Koan09Mapper mapper = session.getMapper(Koan09Mapper.class);
			City city = mapper.getCityById(544);
			
			assertEquals(544, city.getId());
			assertEquals("Toulouse", city.getCity());
			assertNotNull(city.getLastUpdate());
			
			Country co = city.getCountry();
			assertNotNull(co);
			assertEquals("France", co.getCountry());
			assertNotNull(co.getLastUpdate());
			
		} finally {
			if (session != null) session.close();
		}
	}

	@Test
	public void learnToUseNestedSelectForAssociation() {
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			int cityCount = session.selectOne("getCityCount");
			
			Koan09Mapper mapper = session.getMapper(Koan09Mapper.class);
			List<City> lc = mapper.getCities();

			City first = lc.iterator().next();
			assertEquals(1, first.getId());
			
			assertEquals(cityCount, lc.size());
			City city = lc.get(543);
			assertEquals(544, city.getId());
			assertEquals("Toulouse", city.getCity());
			assertNotNull(city.getLastUpdate());
			
			Country co = city.getCountry();
			assertNotNull(co);
			assertEquals("France", co.getCountry());
			assertNotNull(co.getLastUpdate());
			
		} finally {
			if (session != null) session.close();
		}
	}

}
