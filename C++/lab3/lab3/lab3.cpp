// lab3.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include "pch.h"
#include <iostream>
#include <vector>
#include "ctpl.h"
#include <ctime>
#include <chrono>
#include <future>


#define size1 100

int m1[size1][size1], m2[size1][size1], mresult[size1][size1];

using namespace std;


void adder1(int id, vector<pair<int, int>> & pos) {
	for (pair<int, int> var : pos)
	{
		mresult[var.first][var.second] = m1[var.first][var.second] + m2[var.first][var.second];
	}
}

void multiplier1(int id, vector<pair<int, int>> & pos) {
	for (pair<int, int> var : pos)
	{
		int val = 0;
		for (int i = 0; i < size1; ++i)
		{
			val += m1[var.first][i] * m2[i][var.second];
		}
		mresult[var.first][var.second] = val;

	}
}

int sum(int a, int b) { return a + b; }

vector<pair<pair<int, int>, int>> adder2(vector<pair<int, int>>  pos) {

	vector<pair<pair<int, int>, int>> result;

	for (pair<int, int> var : pos)
	{
		result.push_back( pair<pair<int,int>,int>(var, m1[var.first][var.second] + m2[var.first][var.second]));
	}
	return result;
}

vector<pair<pair<int,int>, int>> multiplier2(vector<pair<int, int>>  pos) {

	vector<pair<pair<int, int>, int>> result;

	for (pair<int, int> var : pos)
	{
		int val = 0;
		for (int i = 0; i < size1; ++i)
		{
			val += m1[var.first][i] * m2[i][var.second];
		}
		result.push_back(pair<pair<int, int>, int>(var, val));

	}

	return result;

}

int main()
{
	bool non_async = true;
	bool asyncb = true;
	long avg = 0;
	int it = 250;

	if (non_async) {

		avg = 0;

		for (int ii = 0; ii < it; ++ii)
		{
			auto start = chrono::high_resolution_clock::now();
			int nrthreads = size1*size1;

			vector<pair<int, int>> positions;
			vector<vector<pair<int, int>>> adder_positions;

			for (int i = 0; i < nrthreads; ++i)
			{
				adder_positions.push_back(vector<pair<int, int>>());
			}

			for (int i = 0; i < size1; ++i) {
				for (int j = 0; j < size1; ++j)
				{
					positions.push_back(pair<int, int>(i, j));
					m1[i][j] = i * size1 + j;
					m2[i][j] = i * size1 + j;
				}
			}

			for (int i = 0; i < positions.size(); ++i)
			{
				adder_positions[i%nrthreads].push_back(positions[i]);
			}

			ctpl::thread_pool p(5);
			for (int i = 0; i < adder_positions.size(); ++i)
			{
				p.push(adder1, adder_positions[i]);
			}
			p.stop();
			//std::cout << "Added in " << (std::chrono::high_resolution_clock::now() - start).count() / 1000000 << " ms " << nrthreads << " threads" << endl;
			avg += (std::chrono::high_resolution_clock::now() - start).count() / 1000000;
		}
		std::cout << "Addition avg of "<<it<<" runs "<<avg / it << endl;

		avg = 0;

		for (int ii = 0; ii < it; ++ii)
		{
			auto start = chrono::high_resolution_clock::now();
			int nrthreads = size1 * size1;

			vector<pair<int, int>> positions;
			vector<vector<pair<int, int>>> adder_positions;

			for (int i = 0; i < nrthreads; ++i)
			{
				adder_positions.push_back(vector<pair<int, int>>());
			}

			for (int i = 0; i < size1; ++i) {
				for (int j = 0; j < size1; ++j)
				{
					positions.push_back(pair<int, int>(i, j));
					m1[i][j] = i * size1 + j;
					m2[i][j] = i * size1 + j;
				}
			}

			for (int i = 0; i < positions.size(); ++i)
			{
				adder_positions[i%nrthreads].push_back(positions[i]);
			}

			ctpl::thread_pool p(5);
			for (int i = 0; i < adder_positions.size(); ++i)
			{
				p.push(multiplier1, adder_positions[i]);
			}
			p.stop();
			//std::cout << "Multiplied in " << (std::chrono::high_resolution_clock::now() - start).count() / 1000000 << " ms " << nrthreads << " threads" << endl;
			avg += (std::chrono::high_resolution_clock::now() - start).count() / 1000000;
		}
		std::cout << "Multiplication avg. of "<<it<<" runs " << avg / it << endl;
	}
	if(asyncb)
	{
		avg = 0;
		for (int ii = 0; ii < it; ++ii)
		{
			auto start = chrono::high_resolution_clock::now();
			int nrthreads = size1 * size1;

			vector<pair<int, int>> positions;
			vector<vector<pair<int, int>>> adder_positions;

			for (int i = 0; i < nrthreads; ++i)
			{
				adder_positions.push_back(vector<pair<int, int>>());
			}

			for (int i = 0; i < size1; ++i) {
				for (int j = 0; j < size1; ++j)
				{
					positions.push_back(pair<int, int>(i, j));
					m1[i][j] = i * size1 + j;
					m2[i][j] = i * size1 + j;
				}
			}

			for (int i = 0; i < positions.size(); ++i)
			{
				adder_positions[i%nrthreads].push_back(positions[i]);
			}

			vector< future< vector<pair<pair<int, int>, int>>>> res;

			ctpl::thread_pool p(5);
			for (int i = 0; i < adder_positions.size(); ++i)
			{
				res.push_back(async(adder2, adder_positions[i]));
			}
			p.stop();
			for (int i = 0; i < res.size(); ++i) {
				vector<pair<pair<int, int>, int>> partresult = res[i].get();
				for (auto var : partresult)
					mresult[var.first.first][var.first.second] = var.second;
			}
			//std::cout << "Added in " << (std::chrono::high_resolution_clock::now() - start).count() / 1000000 << " ms " << nrthreads << " threads" << endl;
			avg += (std::chrono::high_resolution_clock::now() - start).count() / 1000000;
		}
		std::cout << "Addition avg of " << it << " runs " << avg / it << endl;

		avg = 0;

		for (int ii = 0; ii < it; ++ii)
		{
			auto start = chrono::high_resolution_clock::now();
			int nrthreads = size1 * size1;

			vector<pair<int, int>> positions;
			vector<vector<pair<int, int>>> adder_positions;

			for (int i = 0; i < nrthreads; ++i)
			{
				adder_positions.push_back(vector<pair<int, int>>());
			}

			for (int i = 0; i < size1; ++i) {
				for (int j = 0; j < size1; ++j)
				{
					positions.push_back(pair<int, int>(i, j));
					m1[i][j] = i * size1 + j;
					m2[i][j] = i * size1 + j;
				}
			}

			for (int i = 0; i < positions.size(); ++i)
			{
				adder_positions[i%nrthreads].push_back(positions[i]);
			}

			vector< future< vector<pair<pair<int, int>, int>>>> res;

			ctpl::thread_pool p(5);
			for (int i = 0; i < adder_positions.size(); ++i)
			{
				res.push_back(async(multiplier2, adder_positions[i]));
			}
			p.stop();
			for (int i = 0; i < res.size(); ++i) {
				vector<pair<pair<int, int>, int>> partresult = res[i].get();
				for (auto var : partresult)
					mresult[var.first.first][var.first.second] = var.second;
			}
			//std::cout << "Multiplied in " << (std::chrono::high_resolution_clock::now() - start).count() / 1000000 << " ms " << nrthreads << " threads" << endl;
			avg += (std::chrono::high_resolution_clock::now() - start).count() / 1000000;
		}
		std::cout << "Multiplication avg. of " << it << " runs " << avg / it << endl;
	}
	/*for (int i = 0; i < size1; ++i) {
		for (int j = 0; j < size1; ++j)
		{
			cout << mresult[i][j] << "\t";
		}
		cout << endl;
	}*/


}

// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu

// Tips for Getting Started: 
//   1. Use the Solution Explorer window to add/manage files
//   2. Use the Team Explorer window to connect to source control
//   3. Use the Output window to see build output and other messages
//   4. Use the Error List window to view errors
//   5. Go to Project > Add New Item to create new code files, or Project > Add Existing Item to add existing code files to the project
//   6. In the future, to open this project again, go to File > Open > Project and select the .sln file
