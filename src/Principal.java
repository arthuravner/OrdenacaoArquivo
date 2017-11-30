import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal {
		
	//Em Bytes
	private final static int TAMANHODALINHA = 300;
	private static long TAMANHODOARQUIVO; 
	
	private final static String FILEPATHORIG = "c:/workspace/files/cep_teste.dat";
	private final static String FILEPATHDEST = "c:/workspace/files/editado/cep_ordenado_por_blocos.dat";
	
	static List<Endereco> listaEnderecos;
	
	static RandomAccessFile fDest;
	static RandomAccessFile f;
	
	public static void main(String[] args) throws Exception {
		       
		f = new RandomAccessFile(FILEPATHORIG, "r");
		fDest = new RandomAccessFile(FILEPATHDEST, "rw");
//		fDest.seek(0);
		
		listaEnderecos = new ArrayList<>();
	      				
		int QtdBlocos = ObterQtdBlocosDoUsuario();
		
		CarregarArquivoEmMemoria();
		
		OrdenarBlocos(QtdBlocos);			
		
		f.close();
		fDest.close();
	}	
	
	
	private static int ObterQtdBlocosDoUsuario(){
		Scanner scanner = new Scanner(System.in);
        System.out.print("Digite a quantidade de blocos: ");
        int input = scanner.nextInt();
        
        scanner.close();
        
        return input;
	}
	
	private static void CarregarArquivoEmMemoria(){
		System.out.println("\nCarregando arquivo, aguarde.");
		
		try 
		{	          	    	 		
			TAMANHODOARQUIVO = f.length();
			
			for(long i = 0; i < f.length(); i = i + TAMANHODALINHA)
			{
			    f.seek(i);
			    Endereco endereco = new Endereco();
			    endereco.leEndereco(f);
			    listaEnderecos.add(endereco);
			}	        	         	          	          
		}catch (IOException ex) {
			ex.printStackTrace();
		}
		
		System.out.println("Arquivo carregado, " + listaEnderecos.size() + " registros encontrados.\n");
	}
	
	private static int DefinirTamanhoDosBlocosEmLinhas(int QtdBlocos){
		int TamBloco =  (int) TAMANHODOARQUIVO/QtdBlocos;
		
		for (int i = 1; i <= 300; i++){
			if(TamBloco % 300 == 0){
				break;
			}
			TamBloco--;
		}			
				
		System.out.println("#TESTE# Tamanho do bloco em linhas: " + TamBloco/300);
		
		return TamBloco/300;
	}
	
	private static void OrdenarBlocos(int QtdBlocos){		
		
		System.out.println("Ordenando blocos do arquivo, aguarde.");
		
		int QtdLinhasDoBloco = DefinirTamanhoDosBlocosEmLinhas(QtdBlocos);
		
		List<Endereco> listaAuxiliar = new ArrayList<>();
		
		for (int i = 0; i < listaEnderecos.size(); i++){
			listaAuxiliar.add(listaEnderecos.get(i));
						
			if(listaAuxiliar.size() == QtdLinhasDoBloco){
				
				Collections.sort(listaAuxiliar, new Comparator<Endereco>() {
			        @Override
			        public int compare(Endereco endereco1, Endereco endereco2)
			        {

			            return  endereco1.getCep().compareTo(endereco2.getCep());
			        }
			    });
				
				for(Endereco endereco : listaAuxiliar){
					System.out.println(endereco);
					EscreverEnderecoNoTxt(endereco);
				}
				System.out.println(listaAuxiliar.size());
				listaAuxiliar.clear();
			}			
		}
		
		System.out.println("\nArquivo dividido em " + QtdBlocos + " blocos. Cada bloco está ordenado por cep.");
	}
	
	private static void EscreverEnderecoNoTxt(Endereco endereco){
		try{
			Charset enc = Charset.forName("ISO-8859-1");
			byte b[] = endereco.toString().getBytes(enc);
			fDest.write(b);			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
}
