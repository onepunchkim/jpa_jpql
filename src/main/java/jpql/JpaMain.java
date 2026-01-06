package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.Collection;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setAge(10);
            //member1.setType(MemberType.ADMIN);
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            //영속성 컨텍스트에서 관리된다.
            /*List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList(); */

            //DTO클래스와 생성자를 통해서 생성
            /*List<MemberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            MemberDTO memberDTO = result.get(0);
            System.out.println("username = " + memberDTO.getUsername());
            System.out.println("age = " + memberDTO.getAge()); */

            //Paging Query
            //String query = "select m from Member m join m.team t"; //inner join
            //String query = "select m from Member m left join m.team t"; //left outer join
/*            String query = "select m from Member m, Team t where m.username = t.name"; //세타 조인

            List<Member> result = em.createQuery(query, Member.class)
                    //.setFirstResult(1) //페이징
                    //.setMaxResults(10) //페이징
                    .getResultList();

            System.out.println("result.size = " + result.size());
            for (Member member1 : result) {
                System.out.println("member1 = " + member1);
            }*/

            //Sub Query
/*
            String query = "select m.username, 'HELLO', true From Member m " +
                            "where m.type = :userType";

            List<Object[]> result = em.createQuery(query)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);
            }*/

            //CASE
            //String query = "select nullif(m.username, '관리자') from Member m";

            //String query = "select function('group_concat', m.username) from Member m";

            //String query = "select m.team from Member m"; //묵시적 내부 조인 발생 (발생하지 않도록 조심)

            //컬렉션 값 묵시적 내부 조인 발생 (탐색이 어렵다)
            //명시적으로 from 절에서 별칭으로 사용
            //String query = "select m.username from Team t join t.members m";

/*            String query = "select m from Member m join fetch m.team";

            List<Member> result = em.createQuery(query, Member.class).getResultList();

            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
                //회원1, 팀A(SQL)
                //회원2, 팀A(1차 캐시)
                //회원3, 팀B(SQL)
                //회원 100명 -> N+1
            }*/

            String query = "select t from Team t join fetch t.members";

            List<Team> result = em.createQuery(query, Team.class).getResultList();

            for (Team team : result) {
                System.out.println("team = " + team.getName() + "|members = " + team.getMembers().size());
                //일대다 조인은 데이터가 뻥튀기 된다.
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

}
